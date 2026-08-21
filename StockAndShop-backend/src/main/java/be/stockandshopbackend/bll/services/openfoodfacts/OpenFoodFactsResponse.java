package be.stockandshopbackend.bll.services.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Partial mapping of the Open Food Facts API v0 response.
 * {@code status == 1} means the product was found; any other value means not found.
 *
 * <p>Lives next to {@link OpenFoodFactsServiceImpl} (not in the pl DTOs) because it maps
 * an EXTERNAL API contract consumed by the business layer — it is never exposed over REST.</p>
 */
public record OpenFoodFactsResponse(
        @JsonProperty("status") int status,
        @JsonProperty("product") OFFProduct product
) {
    public record OFFProduct(
            @JsonProperty("product_name") String productName,
            @JsonProperty("product_name_fr") String productNameFr,
            @JsonProperty("brands") String brands,
            @JsonProperty("categories") String categories,
            @JsonProperty("quantity") String quantity,
            @JsonProperty("image_front_url") String imageFrontUrl,
            @JsonProperty("nutriscore_grade") String nutriscoreGrade,
            @JsonProperty("ecoscore_grade") String ecoscoreGrade,
            @JsonProperty("packaging_tags") List<String> packagingTags,
            // Utilisés pour retrouver la catégorie interne (même logique que le script d'import)
            @JsonProperty("pnns_groups_1") String pnnsGroups1,
            @JsonProperty("categories_tags") List<String> categoriesTags
    ) {}
}
