package be.stockandshopbackend.pl.DTOs.Response.products;

import be.stockandshopbackend.dl.entities.product.ProductListItem;
import be.stockandshopbackend.dl.entities.product.ProductStockHome;
import be.stockandshopbackend.dl.enums.Unity;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// Shared DTO for both ProductStockHome (stock) and ProductListItem (shopping list)
public record ProductItemResponse(
        Long id,
        String nameProduct,
        // The line's effective unit (chosen unit if set, otherwise the product's default)
        String unityProduct,
        // All units the product can use, for the unit picker (first = default)
        List<String> unities,
        int quantity,
        String category,
        // @JsonProperty kept explicitly — records expose isChecked() which Jackson maps correctly,
        // but the annotation guarantees the JSON key stays "isChecked" regardless of Jackson config.
        @JsonProperty("isChecked") boolean isChecked
) {
    private static List<String> unitiesOf(be.stockandshopbackend.dl.entities.product.Product product) {
        return product.getUnities().stream().map(Unity::getValue).toList();
    }

    public static ProductItemResponse fromProductListItem(ProductListItem item) {
        Unity effective = item.getUnity() != null ? item.getUnity() : item.getProduct().getUnity();
        return new ProductItemResponse(
                item.getId(),
                item.getProduct().getName(),
                effective.getValue(),
                unitiesOf(item.getProduct()),
                item.getQuantity(),
                item.getProduct().getCategory().getName(),
                item.isChecked()
        );
    }

    // Stock items are never in a checked state
    public static ProductItemResponse fromProductStockHome(ProductStockHome stock) {
        Unity effective = stock.getUnity() != null ? stock.getUnity() : stock.getProduct().getUnity();
        return new ProductItemResponse(
                stock.getId(),
                stock.getProduct().getName(),
                effective.getValue(),
                unitiesOf(stock.getProduct()),
                stock.getQuantity(),
                stock.getProduct().getCategory().getName(),
                false
        );
    }
}
