package be.stockandshopbackend.pl.DTOs.Response.products;

import be.stockandshopbackend.dl.entities.product.Product;

public record ProductResponse(
        Long id,
        String name,
        String unity,
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
