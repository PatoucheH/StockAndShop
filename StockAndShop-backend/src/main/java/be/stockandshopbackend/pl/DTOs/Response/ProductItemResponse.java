package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ProductStockHome;
import com.fasterxml.jackson.annotation.JsonProperty;

// Shared DTO for both ProductStockHome (stock) and ProductListItem (shopping list)
public record ProductItemResponse(
        Long id,
        String nameProduct,
        String unityProduct,
        int quantity,
        String category,
        // @JsonProperty kept explicitly — records expose isChecked() which Jackson maps correctly,
        // but the annotation guarantees the JSON key stays "isChecked" regardless of Jackson config.
        @JsonProperty("isChecked") boolean isChecked
) {
    public static ProductItemResponse fromProductListItem(ProductListItem productListItem) {
        return new ProductItemResponse(
                productListItem.getId(),
                productListItem.getProduct().getName(),
                productListItem.getProduct().getUnity().getValue(),
                productListItem.getQuantity(),
                productListItem.getProduct().getCategory().getName(),
                productListItem.isChecked()
        );
    }

    // Stock items are never in a checked state
    public static ProductItemResponse fromProductStockHome(ProductStockHome productStockHome) {
        return new ProductItemResponse(
                productStockHome.getId(),
                productStockHome.getProduct().getName(),
                productStockHome.getProduct().getUnity().getValue(),
                productStockHome.getQuantity(),
                productStockHome.getProduct().getCategory().getName(),
                false
        );
    }
}
