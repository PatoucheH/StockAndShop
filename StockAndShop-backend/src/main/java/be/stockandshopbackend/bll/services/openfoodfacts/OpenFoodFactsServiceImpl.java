package be.stockandshopbackend.bll.services.openfoodfacts;

import be.stockandshopbackend.bll.services.productAndShoppingList.category.CategoryService;
import be.stockandshopbackend.dal.repositories.product.ProductRepository;
import be.stockandshopbackend.dl.entities.product.Category;
import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.enums.Unity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OpenFoodFactsServiceImpl implements OpenFoodFactsService {

    private static final String OFF_URL = "https://world.openfoodfacts.org/api/v0/product/{barcode}.json";
    private static final String BARCODE_REGEX = "^\\d{8,14}$";

    // Catégories génériques en début de nom
    private static final Pattern CATEGORY_PREFIX =
            Pattern.compile("^(boissons?|jus\\s+de\\s+fruits?|jus|eaux?|laits?|bi[eè]res?)\\s+", Pattern.CASE_INSENSITIVE);
    // Quantités partout dans le nom (33cl, 1.5l, 500g…)
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

    private final RestClient restClient;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public OpenFoodFactsServiceImpl(RestClient.Builder restClientBuilder,
                                    ProductRepository productRepository,
                                    CategoryService categoryService) {
        this.restClient = restClientBuilder.build();
        this.productRepository = productRepository;
        this.categoryService = categoryService;
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

        Product product = new Product();
        product.setBarcode(barcode);
        product.setName(name);
        product.setUnity(mapUnity(off.packagingTags()));
        product.setBrand(truncate(off.brands(), 255));
        product.setImageUrl(off.imageFrontUrl());
        product.setPackageQuantity(truncate(off.quantity(), 50));
        product.setNutriscoreGrade(off.nutriscoreGrade());
        product.setEcoscoreGrade(off.ecoscoreGrade());

        Category category = categoryService.findByNameOrCreate(resolveCategory(off.categories()));
        product.setCategory(category);

        return productRepository.save(product);
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

    // OFF returns categories as a comma-separated hierarchy (most specific last); we take the first (broadest) entry
    private String resolveCategory(String categories) {
        if (categories == null || categories.isBlank()) return "other";
        return categories.split(",")[0].strip();
    }

    private Unity mapUnity(List<String> packagingTags) {
        if (packagingTags == null) return Unity.OTHER;
        for (String tag : packagingTags) {
            if (tag.contains("bottle")) return Unity.BOTTLE;
            if (tag.contains("can") || tag.contains("tin")) return Unity.CAN;
            if (tag.contains("jar")) return Unity.JAR;
            if (tag.contains("box") || tag.contains("carton")) return Unity.BOX;
            if (tag.contains("packet") || tag.contains("bag") || tag.contains("sachet")) return Unity.PACKET;
        }
        return Unity.OTHER;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
