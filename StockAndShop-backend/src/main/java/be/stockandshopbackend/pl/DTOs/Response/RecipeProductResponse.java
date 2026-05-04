package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.RecipeProduct;

public record RecipeProductResponse(
        Long productId,
        String productName,
        int quantity,
        String unity
) {

    public static RecipeProductResponse fromRecipeProduct(RecipeProduct rProduct) {
        return new RecipeProductResponse(
                rProduct.getProduct().getId(),
                rProduct.getProduct().getName(),
                rProduct.getQuantity(),
                rProduct.getProduct().getUnity().name()
        );
    }

}
