package be.stockandshopbackend.pl.DTOs.Response.products;

import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.enums.Unity;

import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String unity,
        List<String> unities,
        String category,
        String barcode,
        String brand,
        String imageUrl,
        String packageQuantity,
        String nutriscoreGrade,
        String ecoscoreGrade
) {
    public static ProductResponse fromProduct(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getUnity().getValue(),
                p.getUnities().stream().map(Unity::getValue).toList(),
                p.getCategory().getName(),
                p.getBarcode(),
                p.getBrand(),
                p.getImageUrl(),
                p.getPackageQuantity(),
                p.getNutriscoreGrade(),
                p.getEcoscoreGrade()
        );
    }
}
