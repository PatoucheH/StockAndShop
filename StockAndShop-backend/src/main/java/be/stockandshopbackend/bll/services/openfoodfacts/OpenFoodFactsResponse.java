package be.stockandshopbackend.bll.services.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
            @JsonProperty("packaging_tags") List<String> packagingTags
    ) {}
}
