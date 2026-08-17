package be.stockandshopbackend.pl.DTOs.Response.products;

import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.enums.Unity;

import java.util.List;

/**
 * Lightweight product view for the autocomplete search dropdown.
 * Omits heavy fields (imageUrl, barcode, grades) to keep the payload small,
 * especially over mobile connections.
 */
public record ProductSearchResponse(
        Long id,
        String name,
        String unity,
        List<String> unities,
        String category
) {
    public static ProductSearchResponse fromProduct(Product p) {
        return new ProductSearchResponse(
                p.getId(),
                p.getName(),
                p.getUnity().getValue(),
                p.getUnities().stream().map(Unity::getValue).toList(),
                p.getCategory().getName()
        );
    }
}
