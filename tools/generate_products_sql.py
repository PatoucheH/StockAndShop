import pandas as pd
import math
import sys
import os

INPUT_CSV = r"C:\Users\HugoP\Documents\Dev\Projects\StockAndShop\data\en.openfoodfacts.org.products.csv"
OUTPUT_SQL = r"C:\Users\HugoP\Documents\Dev\Projects\StockAndShop\data\products_off_import.sql"

CHUNK_SIZE = 50_000
BATCH_SIZE = 500
MIN_COMPLETENESS = 0.4

PNNS_TO_CATEGORY_ID = {
    "Beverages":                    4,
    "Milk and dairy products":      6,
    "Fruits and vegetables":        8,
    "Cereals and potatoes":         2,
    "Fish Meat Eggs":               9,
    "Sugary snacks":               11,
    "Salty snacks":                11,
    "Fat and sauces":               2,
    "Composite foods":              2,
}
DEFAULT_CATEGORY_ID = 1
VALID_GRADES = {'a', 'b', 'c', 'd', 'e'}

# Défaut d'unité par catégorie — utilisé si aucun mot-clé ne matche
CATEGORY_DEFAULT_UNITY = {
    1:  "PIECE",    # autres
    2:  "PACKET",   # épicerie
    3:  "CAN",      # conserves
    4:  "BOTTLE",   # boissons
    5:  "PIECE",    # boulangerie
    6:  "PIECE",    # produits laitiers
    7:  "PACKET",   # surgelés
    8:  "GRAMS",    # fruits et légumes (matière première pesée)
    9:  "GRAMS",    # boucherie et charcuterie (matière première pesée)
    10: "BOTTLE",   # hygiène
    11: "PACKET",   # confiserie et snack
}

# Mots-clés dans le nom du produit → unité (ordre = priorité)
NAME_KEYWORDS_TO_UNITY = [
    # GRAMS — matières premières traitées (override explicite)
    (["râpé", "haché", "émincé", "effiloché", "concassé"], "GRAMS"),

    # BOTTLE — liquides en bouteille
    (["lait ", "lait d", "lait e", "lait c", "lait 1", "lait 2",
      "jus ", "jus d", "jus de", "eau ", "eau m", "eau p",
      "soda", "cola", "limonade", "smoothie", "nectar",
      "vin ", "bière", "cidre", "champagne", "prosecco",
      "huile ", "vinaigre", "ketchup", "sauce ", "sirop ",
      "shampoing", "shampooing", "gel douche", "après-shampoing",
      "liquide vaisselle", "lessive ", "eau de javel",
      "mayonnaise", "moutarde"], "BOTTLE"),

    # JAR — bocaux et conserves en pot
    (["confiture", "marmelade", "gelée de", "miel ", "miel d",
      "compote", "pâte à tartiner", "nutella", "speculoos",
      "cornichon", "câpre", "olive", "tapenade",
      "purée d'amande", "purée de noisette", "tahini"], "JAR"),

    # BOX — boîtes (glaces, chocolats assortis)
    (["glace ", "sorbet", "crème glacée", "bac de glace",
      "assortiment de chocolat", "chocolats assortis"], "BOX"),

    # PIECE — fruits et légumes individuels
    (["pomme ", "poire ", "banane", "orange ", "citron",
      "pamplemousse", "mangue", "ananas", "kiwi", "avocat",
      "oignon", "ail ", "échalote", "courgette", "poivron",
      "concombre", "aubergine", "tomate ", "tomates cerises",
      "salade ", "laitue", "endive", "poireau", "navet",
      "chou-fleur", "brocoli", "chou ", "épinard"], "PIECE"),

    # PIECE — produits laitiers individuels
    (["yaourt", "yogourt", "yoghourt", "skyr", "fromage blanc",
      "petit-suisse", "faisselle", "danette", "activia",
      "camembert", "brie ", "chèvre ", "roquefort", "munster",
      "reblochon", "livarot", "maroilles", "époisses",
      "oeuf", "oeufs"], "PIECE"),

    # PIECE — boulangerie et plats individuels
    (["pain ", "pain d", "baguette", "ficelle", "brioche",
      "croissant", "pain au chocolat", "chausson",
      "pizza ", "quiche", "tarte ", "tourte",
      "cake ", "moelleux", "fondant au"], "PIECE"),

    # PACKET — charcuterie conditionnée
    (["jambon ", "jambon b", "jambon c", "jambon d",
      "saucisson", "lardons", "chipolata", "merguez",
      "knack", "saucisse", "chorizo", "coppa",
      "bacon", "pancetta", "bresaola"], "PACKET"),

    # PACKET — épicerie sèche
    (["pâtes", "pasta", "spaghetti", "tagliatelle", "penne",
      "fusilli", "macaroni", "riz ", "semoule", "boulgour",
      "quinoa", "lentille", "pois chiche", "haricot sec",
      "farine ", "sucre ", "sel ", "poivre",
      "levure", "fécule", "maïzena",
      "céréales", "muesli", "granola", "flocons d",
      "biscuit", "gâteau", "cookie", "crackers", "chips",
      "café ", "café m", "café e", "thé ", "infusion",
      "tisane", "cacao", "chocolat en poudre",
      "chocolat noir", "chocolat au lait", "tablette de"], "PACKET"),

    # MILLILITER — seuls les produits vraiment mesurés en ml (cuisine)
    (["crème fraîche", "crème liquide", "crème épaisse",
      "crème entière", "crème allégée", "lait de coco",
      "bouillon de"], "MILLILITER"),
]


def normalize_grade(val):
    if not val or (isinstance(val, float) and math.isnan(val)):
        return None
    v = str(val).strip().lower()
    return v if v in VALID_GRADES else None


def map_unity(name, quantity_str, category_id):
    name_lower = name.lower()

    # 1. Mots-clés dans le nom — priorité absolue
    for keywords, unity in NAME_KEYWORDS_TO_UNITY:
        if any(kw in name_lower for kw in keywords):
            return unity

    # 2. Champ quantity du CSV — si informatif
    unity_from_qty = _unity_from_quantity(quantity_str)
    if unity_from_qty not in ("PIECE", "GRAMS", "MILLILITER"):
        # Bouteille, boîte, paquet, bocal détectés dans le packaging → fiable
        return unity_from_qty

    # 3. Défaut par catégorie
    return CATEGORY_DEFAULT_UNITY.get(category_id, "PIECE")


def _unity_from_quantity(quantity_str):
    if not quantity_str or (isinstance(quantity_str, float) and math.isnan(quantity_str)):
        return "PIECE"
    q = str(quantity_str).lower()
    if "bouteille" in q or "bottle" in q:
        return "BOTTLE"
    if "boîte" in q or "boite" in q or " can" in q:
        return "CAN"
    if "paquet" in q or "packet" in q or "sachet" in q:
        return "PACKET"
    if "bocal" in q or "jar" in q:
        return "JAR"
    if "ml" in q or "cl" in q or " l" in q or q.endswith("l"):
        return "MILLILITER"
    if " g" in q or "kg" in q or q.endswith("g"):
        return "GRAMS"
    return "PIECE"


def sql_val(val):
    if val is None or (isinstance(val, float) and math.isnan(val)) or str(val).strip() == "":
        return "NULL"
    return "'" + str(val).replace("'", "''").strip()[:255] + "'"


def get_name(row):
    name = str(row.get("product_name", "")).strip()
    return name if name != "nan" else ""


def get_image(row):
    val = row.get("image_url")
    if val and not (isinstance(val, float) and math.isnan(val)) and str(val).strip():
        return val
    return None


def process():
    print(f"Lecture de {INPUT_CSV}...")

    header = pd.read_csv(INPUT_CSV, sep="\t", nrows=0)
    available = set(header.columns)
    print(f"Colonnes disponibles dans le CSV : {len(available)}")

    wanted = [
        "code", "product_name", "brands",
        "quantity", "image_url",
        "nutriscore_grade", "environmental_score_grade", "pnns_groups_1",
        "categories_tags", "countries_tags", "completeness"
    ]
    cols = [c for c in wanted if c in available]
    missing = [c for c in wanted if c not in available]
    if missing:
        print(f"Colonnes absentes du CSV (seront ignorées) : {missing}")

    seen_barcodes = set()
    seen_names = set()
    total_imported = 0

    with open(OUTPUT_SQL, "w", encoding="utf-8") as out:
        out.write("-- Import Open Food Facts — généré automatiquement\n")
        out.write("-- Exécuter UNE SEULE FOIS sur une DB vierge de produits OFF\n\n")
        out.write("SET client_encoding = 'UTF8';\n\n")

        rows_buffer = []

        for chunk_num, chunk in enumerate(pd.read_csv(
            INPUT_CSV, sep="\t", usecols=cols,
            chunksize=CHUNK_SIZE, low_memory=False, on_bad_lines="skip"
        )):
            for _, row in chunk.iterrows():
                barcode = str(row.get("code", "")).strip()
                if not barcode or barcode == "nan":
                    continue
                if not barcode.isdigit() or len(barcode) < 8:
                    continue

                countries = str(row.get("countries_tags", "")).lower()
                if "en:france" not in countries and "en:belgium" not in countries and "en:luxembourg" not in countries:
                    continue

                name = get_name(row)
                if not name or name == "nan":
                    continue

                try:
                    completeness = float(row.get("completeness", 0))
                except (ValueError, TypeError):
                    completeness = 0
                if completeness < MIN_COMPLETENESS:
                    continue

                barcode_lower = barcode.lower()
                name_lower = name.lower()
                if barcode_lower in seen_barcodes or name_lower in seen_names:
                    continue
                seen_barcodes.add(barcode_lower)
                seen_names.add(name_lower)

                pnns = str(row.get("pnns_groups_1", "")).strip()
                tags = str(row.get("categories_tags", "")).lower()
                if "en:frozen-foods" in tags or "en:frozen" in tags:
                    category_id = 7
                elif "en:canned-foods" in tags or "en:canned" in tags or "en:jarred-foods" in tags:
                    category_id = 3
                else:
                    category_id = PNNS_TO_CATEGORY_ID.get(pnns, DEFAULT_CATEGORY_ID)

                unity = map_unity(name, row.get("quantity"), category_id)
                image = get_image(row)

                rows_buffer.append((
                    sql_val(name_lower),
                    f"'{unity}'",
                    str(category_id),
                    sql_val(barcode),
                    sql_val(row.get("brands")),
                    sql_val(image),
                    sql_val(row.get("quantity")),
                    sql_val(normalize_grade(row.get("nutriscore_grade"))),
                    sql_val(normalize_grade(row.get("environmental_score_grade"))),
                ))

                if len(rows_buffer) >= BATCH_SIZE:
                    write_batch(out, rows_buffer)
                    total_imported += len(rows_buffer)
                    rows_buffer = []
                    print(f"  {total_imported} produits traités...", end="\r")

            print(f"  Chunk {chunk_num + 1} traité, {total_imported} produits jusqu'ici...")

        if rows_buffer:
            write_batch(out, rows_buffer)
            total_imported += len(rows_buffer)

        out.write("\n-- Réinitialisation de la séquence après import\n")
        out.write("SELECT setval(pg_get_serial_sequence('product', 'id'), COALESCE(MAX(id), 1), true) FROM product;\n")

    print(f"\nTerminé : {total_imported} produits exportés dans {OUTPUT_SQL}")


def write_batch(out, rows):
    out.write(
        "INSERT INTO product (name, unity, category_id, barcode, brand, image_url, package_quantity, "
        "nutriscore_grade, ecoscore_grade, created_at, updated_at) VALUES\n"
    )
    lines = []
    for r in rows:
        lines.append(f"  ({r[0]}, {r[1]}, {r[2]}, {r[3]}, {r[4]}, {r[5]}, {r[6]}, {r[7]}, {r[8]}, NOW(), NOW())")
    out.write(",\n".join(lines))
    out.write("\nON CONFLICT DO NOTHING;\n\n")


if __name__ == "__main__":
    if not os.path.exists(INPUT_CSV):
        print(f"ERREUR : fichier {INPUT_CSV} introuvable.")
        print("Télécharger le CSV depuis https://world.openfoodfacts.org/data")
        sys.exit(1)
    process()
