# -*- coding: utf-8 -*-
"""
Génère products_off_import.sql à partir du dump CSV d'Open Food Facts.

Objectifs :
  * NOMS NORMALISÉS  : minuscule, sans accents, sans quantité, sans ponctuation.
                       -> recherche insensible casse/accents, et dédoublonnage fiable.
  * DÉDOUBLONNAGE    : deux noms quasi-identiques ("Coca-Cola 33cl", "COCA COLA",
                       "coca cola") = un seul produit ; on garde la version la plus complète.
  * UNITÉS COHÉRENTES: liquide -> MILLILITER, solide -> GRAMS ;
                       exceptions -> conserves=CAN, chips/snacks=PACKET,
                       œufs & pain/viennoiseries=PIECE.
"""
import pandas as pd
import math, re, sys, os, unicodedata

# --------------------------------------------------------------------------- #
INPUT_CSV  = r"C:\Users\HugoP\Documents\Dev\Projects\StockAndShop\data\en.openfoodfacts.org.products.csv"
OUTPUT_SQL = r"C:\Users\HugoP\Documents\Dev\Projects\StockAndShop\data\products_off_import.sql"

CHUNK_SIZE       = 50_000
BATCH_SIZE       = 500
MIN_COMPLETENESS = 0.5      # qualité minimale de la fiche OFF (0..1)
MIN_SCANS        = 100      # popularité mini (unique_scans_n) ; 0 = filtre désactivé
COUNTRIES        = ("en:france", "en:belgium", "en:luxembourg")
# --------------------------------------------------------------------------- #

PNNS_TO_CATEGORY_ID = {
    "Beverages": 4, "Milk and dairy products": 6, "Fruits and vegetables": 8,
    "Cereals and potatoes": 2, "Fish Meat Eggs": 9, "Sugary snacks": 11,
    "Salty snacks": 11, "Fat and sauces": 2, "Composite foods": 2,
}
DEFAULT_CATEGORY_ID = 1
VALID_GRADES = {'a', 'b', 'c', 'd', 'e'}

# ============================ NORMALISATION ================================= #
_LIGATURES = str.maketrans({"œ": "oe", "æ": "ae", "Œ": "oe", "Æ": "ae"})
_QTY_RE = re.compile(
    r"\b\d+[.,]?\d*\s?(?:l|cl|ml|dl|kg|g|gr|mg|cc|oz|lb|pcs?|pieces?)\b"
    r"|\bx\s?\d+\b|\b\d+\s?x\b|\d+\s?%", re.IGNORECASE)
_PAREN_RE = re.compile(r"\([^)]*\)")
_MULTISPACE_RE = re.compile(r"\s+")
_KEEP_RE = re.compile(r"[^a-z0-9 ]")
# Lettres que le français n'utilise pas -> nom probablement étranger (espagnol, portugais...)
_FOREIGN_CHARS = set("áíóúñÁÍÓÚÑãõÃÕåÅøØ¿¡ßłŁðþŠŽšžșțŞȘ")

def strip_accents(text):
    text = text.translate(_LIGATURES)
    return "".join(c for c in unicodedata.normalize("NFKD", text) if not unicodedata.combining(c))

def normalize_name(raw):
    if raw is None or (isinstance(raw, float) and math.isnan(raw)):
        return ""
    name = str(raw).strip()
    if not name or name.lower() == "nan":
        return ""
    if any(c in _FOREIGN_CHARS for c in name):   # nom étranger -> écarté
        return ""
    name = _PAREN_RE.sub(" ", name)
    name = strip_accents(name).lower().replace("'", " ")
    name = _QTY_RE.sub(" ", name)
    name = _KEEP_RE.sub(" ", name)
    tokens = [t for t in name.split() if not t.isdigit()]
    name = _MULTISPACE_RE.sub(" ", " ".join(tokens)).strip()
    if len(name) < 3 or len(name) > 50:
        return ""
    if not any(t.isalpha() and len(t) >= 3 for t in tokens):
        return ""
    return name

# ============================== UNITÉS ===================================== #
EGG_KW      = ["oeuf"]
BREAD_KW    = ["pain","baguette","croissant","brioche","viennoiserie","chausson",
               "ficelle","biscotte","pain de mie","pain au chocolat"]
CONSERVE_KW = ["conserve","thon","sardine","maquereau","mais","haricot","pois chiche",
               "petit pois","ravioli","cassoulet","flageolet"]
SNACK_KW    = ["chips","cracker","tuc","curly","pop corn","popcorn","bretzel"]
LIQUID_KW   = ["lait","jus","eau","soda","cola","limonade","boisson","sirop","nectar",
               "smoothie","huile","vinaigre","creme liquide","bouillon","ice tea",
               "the glace","liquide vaisselle","lessive","shampoing","shampooing",
               "gel douche","javel","adoucissant"]
# Alcools -> vendus à la bouteille/cannette, pas au ml
ALCOHOL_KW  = ["biere","pils","lager","ipa","stout","vin","cidre","champagne","prosecco",
               "whisky","vodka","rhum","pastis","liqueur","cognac","armagnac","gin",
               "tequila","aperitif","spiritueux"]

def _compile(kws): return [re.compile(r"\b" + re.escape(k) + r"s?\b") for k in kws]
_EGG,_BREAD,_CONS,_SNACK,_LIQUID,_ALCO = map(_compile,[EGG_KW,BREAD_KW,CONSERVE_KW,SNACK_KW,LIQUID_KW,ALCOHOL_KW])
def _m(pats,name): return any(p.search(name) for p in pats)

def _liquid_from_quantity(q):
    if not q or (isinstance(q, float) and math.isnan(q)): return None
    q = str(q).lower()
    if re.search(r"\d\s?(?:ml|cl|dl)\b", q) or re.search(r"\d\s?l\b", q) or "litre" in q: return True
    if re.search(r"\d\s?(?:g|kg|gr|mg)\b", q): return False
    return None

def display_name(raw):
    """Joli nom AFFICHÉ : accents conservés, quantité retirée, casse corrigée."""
    if raw is None or (isinstance(raw, float) and math.isnan(raw)):
        return ""
    s = str(raw).strip()
    if not s or s.lower() == "nan":
        return ""
    s = _PAREN_RE.sub(" ", s)
    s = _QTY_RE.sub(" ", s)
    s = "".join(c if (c.isalnum() or c in " '-") else " " for c in s)
    s = " ".join(t for t in s.split() if not t.isdigit())
    s = _MULTISPACE_RE.sub(" ", s).strip()
    if not s:
        return ""
    if not any(c.islower() for c in s):
        s = s.title()
    return (s[0].upper() + s[1:])[:120]


def map_unity(name, category_id, quantity_str=None):
    if _m(_ALCO, name):                           return "BOTTLE"   # bières, vins, spiritueux
    if _m(_EGG, name):                            return "PIECE"
    if category_id == 5 or _m(_BREAD, name):      return "PIECE"
    if category_id == 3 or _m(_CONS, name):       return "CAN"
    if _m(_SNACK, name):                          return "PACKET"
    # La quantité de l'emballage prime sur les mots-clés ambigus
    # (ex: "Boulettes sauce tomate 400 g" = solide, pas du ml)
    liq = _liquid_from_quantity(quantity_str)
    if liq is False:                              return "GRAMS"
    if liq is True:                               return "MILLILITER"
    if _m(_LIQUID, name):                         return "MILLILITER"
    if category_id == 4:                          return "MILLILITER"
    return "GRAMS"

# Jeux d'unités possibles par catégorie (1re = défaut). Le défaut "logique" du produit
# (map_unity) est placé en tête s'il fait partie du jeu ; sinon on garde le jeu de la catégorie.
def unities_for(name, category_id, quantity_str=None):
    default = map_unity(name, category_id, quantity_str)
    if category_id == 4:                                          base = ["BOTTLE", "CAN"]           # boissons (catégorie uniquement)
    elif category_id == 3:                                        base = ["CAN", "JAR", "PIECE", "GRAMS"]  # conserves
    elif category_id == 5:                                        base = ["PIECE", "PACKET"]         # boulangerie
    elif category_id == 6:                                        base = ["BOTTLE", "PIECE", "JAR", "GRAMS"]  # laitiers
    elif category_id == 8:                                        base = ["PIECE", "GRAMS", "JAR"]   # fruits & legumes
    elif category_id == 9:                                        base = ["GRAMS", "PACKET", "PIECE"]  # boucherie
    elif category_id == 11:                                       base = ["PACKET", "GRAMS", "PIECE"]  # snacks
    elif category_id == 2:                                        base = ["PACKET", "GRAMS", "JAR"]  # epicerie
    elif category_id == 7:                                        base = ["PACKET", "GRAMS", "PIECE"]  # surgeles
    elif category_id == 10:                                       base = ["PIECE", "BOTTLE", "GRAMS"]  # hygiene
    else:                                                         base = ["PIECE", "PACKET", "GRAMS"]  # autres
    if default in base:
        return [default] + [u for u in base if u != default]
    return base

# ============================== HELPERS ==================================== #
def category_of(pnns, tags):
    tags = str(tags).lower()
    if "en:frozen" in tags:                                  return 7
    if "en:canned" in tags or "en:jarred" in tags:           return 3
    return PNNS_TO_CATEGORY_ID.get(str(pnns).strip(), DEFAULT_CATEGORY_ID)

def normalize_grade(val):
    if not val or (isinstance(val, float) and math.isnan(val)): return None
    v = str(val).strip().lower()
    return v if v in VALID_GRADES else None

def sql_val(val):
    if val is None or (isinstance(val, float) and math.isnan(val)) or str(val).strip() == "":
        return "NULL"
    return "'" + str(val).replace("'", "''").strip()[:255] + "'"

def to_float(v, default=0.0):
    try: return float(v)
    except (ValueError, TypeError): return default

# ================================ MAIN ===================================== #
def process():
    print(f"Lecture de {INPUT_CSV}...")
    header = pd.read_csv(INPUT_CSV, sep="\t", nrows=0)
    available = set(header.columns)
    wanted = ["code","product_name","brands","quantity","image_url","nutriscore_grade",
              "environmental_score_grade","pnns_groups_1","categories_tags",
              "countries_tags","completeness","unique_scans_n"]
    cols = [c for c in wanted if c in available]
    has_scans = "unique_scans_n" in available
    print(f"Colonnes utilisées : {cols}")
    if not has_scans and MIN_SCANS > 0:
        print("  (unique_scans_n absent -> filtre popularité ignoré)")

    best = {}            # nom_normalisé -> (score, tuple_valeurs_sql)
    seen_barcodes = set()
    read = kept = 0

    for chunk in pd.read_csv(INPUT_CSV, sep="\t", usecols=cols, chunksize=CHUNK_SIZE,
                             low_memory=False, on_bad_lines="skip"):
        for _, row in chunk.iterrows():
            read += 1
            barcode = str(row.get("code", "")).strip()
            if not barcode.isdigit() or len(barcode) < 8:                 continue
            if barcode in seen_barcodes:                                  continue
            countries = str(row.get("countries_tags", "")).lower()
            if not any(c in countries for c in COUNTRIES):                continue
            if to_float(row.get("completeness")) < MIN_COMPLETENESS:      continue
            if MIN_SCANS > 0 and has_scans and to_float(row.get("unique_scans_n")) < MIN_SCANS:
                continue

            key = normalize_name(row.get("product_name"))   # clé de dédoublonnage
            if not key:                                                   continue
            disp = display_name(row.get("product_name"))        # nom affiché
            if not disp:                                                  continue

            completeness = to_float(row.get("completeness"))
            scans = to_float(row.get("unique_scans_n")) if has_scans else 0.0
            score = (completeness, scans)

            prev = best.get(key)
            if prev and prev[0] >= score:
                continue  # on garde déjà une meilleure version

            category_id = category_of(row.get("pnns_groups_1"), row.get("categories_tags"))
            unities = ",".join(unities_for(key, category_id, row.get("quantity")))
            values = (
                sql_val(disp), f"'{unities}'", str(category_id), sql_val(barcode),
                sql_val(row.get("brands")), sql_val(row.get("image_url")),
                sql_val(row.get("quantity")),
                sql_val(normalize_grade(row.get("nutriscore_grade"))),
                sql_val(normalize_grade(row.get("environmental_score_grade"))),
            )
            if prev is None:
                seen_barcodes.add(barcode)
                kept += 1
            best[key] = (score, values)
        print(f"  lus={read:>9}  gardés(uniques)={len(best):>7}", end="\r")

    print(f"\nÉcriture de {len(best)} produits dans {OUTPUT_SQL}...")
    rows = [v for _, v in best.values()]
    with open(OUTPUT_SQL, "w", encoding="utf-8") as out:
        out.write("-- Import Open Food Facts (unités possibles par catégorie) — généré automatiquement\n")
        out.write("-- À exécuter sur une table product vidée de ses produits.\n")
        out.write("SET client_encoding = 'UTF8';\n\n")
        for i in range(0, len(rows), BATCH_SIZE):
            write_batch(out, rows[i:i+BATCH_SIZE])
        out.write("\n-- Réinitialisation de la séquence après import\n")
        out.write("SELECT setval(pg_get_serial_sequence('product','id'), COALESCE(MAX(id),1), true) FROM product;\n")
    print(f"Terminé : {read} lignes lues -> {len(best)} produits.")

def write_batch(out, rows):
    out.write("INSERT INTO product (name, unities, category_id, barcode, brand, image_url, "
              "package_quantity, nutriscore_grade, ecoscore_grade, created_at, updated_at) VALUES\n")
    out.write(",\n".join(
        f"  ({r[0]}, {r[1]}, {r[2]}, {r[3]}, {r[4]}, {r[5]}, {r[6]}, {r[7]}, {r[8]}, NOW(), NOW())"
        for r in rows))
    out.write("\nON CONFLICT DO NOTHING;\n\n")

if __name__ == "__main__":
    if not os.path.exists(INPUT_CSV):
        print(f"ERREUR : {INPUT_CSV} introuvable.")
        print("Télécharger le CSV : https://world.openfoodfacts.org/data")
        sys.exit(1)
    process()
