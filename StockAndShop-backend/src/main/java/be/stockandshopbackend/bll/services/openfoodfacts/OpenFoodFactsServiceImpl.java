package be.stockandshopbackend.bll.services.openfoodfacts;

import be.stockandshopbackend.dal.repositories.product.CategoryRepository;
import be.stockandshopbackend.dal.repositories.product.ProductRepository;
import be.stockandshopbackend.dl.entities.product.Category;
import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.enums.Unity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OpenFoodFactsServiceImpl implements OpenFoodFactsService {

    private static final String OFF_URL = "https://world.openfoodfacts.org/api/v0/product/{barcode}.json";
    private static final String BARCODE_REGEX = "^\\d{8,14}$";

    // Catégories génériques en début de nom
    private static final Pattern CATEGORY_PREFIX =
            Pattern.compile("^boissons?.*$", Pattern.CASE_INSENSITIVE);    // Quantités partout dans le nom (33cl, 1.5l, 500g…)
    private static final Pattern QUANTITY =
            Pattern.compile("\\s*\\d+[,.]?\\d*\\s*(ml|cl|l|g|kg|oz|lb)\\b", Pattern.CASE_INSENSITIVE);
    // Mots d'emballage en fin de nom
    private static final Pattern CONTAINER_SUFFIX =
            Pattern.compile("\\s+(can|canette|bouteille|boite|bidon|pack|sachet)$", Pattern.CASE_INSENSITIVE);
    // Marques de supermarchés et distributeurs belges/français
    private static final Pattern STORE_BRAND =
            Pattern.compile("\\b(carrefour|delhaize|lidl|aldi|colruyt|boni|cora|intermarch[eé]|spar|okay|albert\\s+heijn|leclerc|monoprix|franprix|casino|g[eé]ant|syst[eè]me\\s+u|match|proxy|365|bio\\s+village)\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    // Qualificatifs purement marketing
    private static final Pattern MARKETING_WORD =
            Pattern.compile("\\b(premium|s[eé]lection|tradition|artisan|terroir|sp[eé]cial|excellence|prestige|gourmet|saveur|classic|original)\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    // Adjectifs de taille/format
    private static final Pattern SIZE_WORD =
            Pattern.compile("\\b(grand[e]?|petit[e]?|gros|grosse|mini|maxi|extra|super|mega|xl|xxl|family|familial[e]?)\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    // Pourcentages (3.5%, 0% mg, 20% mg...)
    private static final Pattern PERCENTAGE =
            Pattern.compile("\\s*\\d+[,.]?\\d*\\s*%\\s*(mg|mat\\.?\\s*gr\\.?)?", Pattern.CASE_INSENSITIVE);

    // ---- Catégorisation & unités : MÊME logique que le script d'import Python ----
    // PNNS d'Open Food Facts -> id de catégorie interne (1..11)
    private static final Map<String, Integer> PNNS_TO_CATEGORY_ID = Map.of(
            "Beverages", 4, "Milk and dairy products", 6, "Fruits and vegetables", 8,
            "Cereals and potatoes", 2, "Fish Meat Eggs", 9, "Sugary snacks", 11,
            "Salty snacks", 11, "Fat and sauces", 2, "Composite foods", 2
    );
    private static final int DEFAULT_CATEGORY_ID = 1;   // "autres"
    private static final Set<Unity> WEIGHT_VOL = Set.of(Unity.GRAMS, Unity.MILLILITER);
    // Vrais œufs (poule/caille) : "(^| )" évite "boeuf"/"bœuf" ; on exclut les œufs de poisson
    private static final Pattern EGG = Pattern.compile("(^| )(œufs?|oeufs?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern EGG_EXCLUDE =
            Pattern.compile("tarama|cabillaud|truite|saumon|poisson|lump", Pattern.CASE_INSENSITIVE);

    private final RestClient restClient;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public OpenFoodFactsServiceImpl(RestClient.Builder restClientBuilder,
                                    ProductRepository productRepository,
                                    CategoryRepository categoryRepository) {
        this.restClient = restClientBuilder.build();
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Product> fetchAndSave(String barcode) {
        if (!barcode.matches(BARCODE_REGEX)) {
            log.warn("OFF: barcode rejected by regex: {}", barcode);
            return Optional.empty();
        }

        log.info("OFF: fetching barcode {}", barcode);
        try {
            OpenFoodFactsResponse response = restClient.get()
                    .uri(OFF_URL, barcode)
                    .retrieve()
                    .body(OpenFoodFactsResponse.class);

            if (response == null || response.status() != 1 || response.product() == null) {
                log.info("OFF: product not found for barcode {}", barcode);
                return Optional.empty();
            }

            log.info("OFF: product found for barcode {}, saving...", barcode);
            return Optional.of(mapAndSave(barcode, response.product()));
        } catch (Exception e) {
            log.error("OFF: error fetching barcode {}: {} - {}", barcode, e.getClass().getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    private Product mapAndSave(String barcode, OpenFoodFactsResponse.OFFProduct off) {
        String name = resolveName(barcode, off);
        log.info("OFF: resolved name='{}' (productName='{}', productNameFr='{}')", name, off.productName(), off.productNameFr());

        // Product already exists in DB under same name (e.g. from CSV import) → update its barcode
        Optional<Product> existing = productRepository.findByName(name);
        if (existing.isPresent()) {
            Product product = existing.get();
            product.setBarcode(barcode);
            if (off.brands() != null)        product.setBrand(truncate(off.brands(), 255));
            if (off.imageFrontUrl() != null)  product.setImageUrl(off.imageFrontUrl());
            if (off.quantity() != null)       product.setPackageQuantity(truncate(off.quantity(), 50));
            if (off.nutriscoreGrade() != null) product.setNutriscoreGrade(off.nutriscoreGrade());
            if (off.ecoscoreGrade() != null)  product.setEcoscoreGrade(off.ecoscoreGrade());
            return productRepository.save(product);
        }

        int categoryId = categoryIdOf(off.pnnsGroups1(), off.categoriesTags());

        Product product = new Product();
        product.setBarcode(barcode);
        product.setName(name);
        product.setUnities(unitiesFor(name, categoryId));
        product.setBrand(truncate(off.brands(), 255));
        product.setImageUrl(off.imageFrontUrl());
        product.setPackageQuantity(truncate(off.quantity(), 50));
        product.setNutriscoreGrade(off.nutriscoreGrade());
        product.setEcoscoreGrade(off.ecoscoreGrade());

        // Catégories 1..11 déjà seed en base ; fallback sur "autres" (1) par sécurité.
        Category category = categoryRepository.findById((long) categoryId)
                .or(() -> categoryRepository.findById((long) DEFAULT_CATEGORY_ID))
                .orElseThrow(() -> new IllegalStateException("Aucune catégorie en base"));
        product.setCategory(category);
        log.info("OFF: barcode {} -> categorie {} ({}), unites {}", barcode, categoryId, category.getName(), product.getUnities());

        return productRepository.save(product);
    }

    // Réplique de category_of() du script Python : tags frozen/canned prioritaires, sinon table PNNS.
    private int categoryIdOf(String pnns, List<String> categoriesTags) {
        String tags = categoriesTags == null ? "" : String.join(",", categoriesTags).toLowerCase();
        if (tags.contains("en:frozen"))                                 return 7;   // surgelés
        if (tags.contains("en:canned") || tags.contains("en:jarred"))   return 3;   // conserves
        if (pnns == null)                                               return DEFAULT_CATEGORY_ID;
        return PNNS_TO_CATEGORY_ID.getOrDefault(pnns.strip(), DEFAULT_CATEGORY_ID);
    }

    // Réplique de unities_for() du script Python : liste ordonnée par catégorie,
    // GRAMS/MILLILITER toujours en fin, cas spécial œufs (pièce).
    private List<Unity> unitiesFor(String name, int categoryId) {
        if (categoryId == 9 && EGG.matcher(name).find() && !EGG_EXCLUDE.matcher(name).find()) {
            return List.of(Unity.PIECE, Unity.GRAMS, Unity.PACKET);
        }
        List<Unity> base = switch (categoryId) {
            case 4  -> List.of(Unity.BOTTLE, Unity.CAN);                          // boissons
            case 3  -> List.of(Unity.TIN, Unity.JAR, Unity.PIECE, Unity.GRAMS);   // conserves
            case 5  -> List.of(Unity.PIECE, Unity.PACKET);                        // boulangerie
            case 6  -> List.of(Unity.BOTTLE, Unity.PIECE, Unity.JAR, Unity.GRAMS);// laitiers
            case 8  -> List.of(Unity.PIECE, Unity.GRAMS, Unity.JAR);              // fruits & légumes
            case 9  -> List.of(Unity.PACKET, Unity.PIECE, Unity.GRAMS);           // boucherie
            case 11 -> List.of(Unity.PACKET, Unity.GRAMS, Unity.PIECE);           // snacks
            case 2  -> List.of(Unity.PACKET, Unity.GRAMS, Unity.JAR);             // épicerie
            case 7  -> List.of(Unity.PACKET, Unity.GRAMS, Unity.PIECE);           // surgelés
            case 10 -> List.of(Unity.PIECE, Unity.BOTTLE, Unity.GRAMS);           // hygiène
            default -> List.of(Unity.PIECE, Unity.PACKET, Unity.GRAMS);           // autres
        };
        // Contenant/comptage devant, poids/volume relégué en fin.
        List<Unity> ordered = new ArrayList<>();
        base.stream().filter(u -> !WEIGHT_VOL.contains(u)).forEach(ordered::add);
        base.stream().filter(WEIGHT_VOL::contains).forEach(ordered::add);
        return ordered;
    }

    private String resolveName(String barcode, OpenFoodFactsResponse.OFFProduct off) {
        String name = off.productNameFr();
        if (name == null || name.isBlank()) name = off.productName();
        if (name == null || name.isBlank()) {
            log.warn("OFF: no product name in API response for barcode {}", barcode);
            return barcode;
        }
        name = sanitize(name);
        return truncate(name, 255);
    }

    private String sanitize(String raw) {
        String name = raw.strip().toLowerCase();
        name = STORE_BRAND.matcher(name).replaceAll("");
        name = CATEGORY_PREFIX.matcher(name).replaceFirst("");
        name = QUANTITY.matcher(name).replaceAll("");
        name = PERCENTAGE.matcher(name).replaceAll("");
        name = CONTAINER_SUFFIX.matcher(name).replaceFirst("");
        name = MARKETING_WORD.matcher(name).replaceAll("");
        name = SIZE_WORD.matcher(name).replaceAll("");
        return name.replaceAll("\\s+", " ").strip();
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
