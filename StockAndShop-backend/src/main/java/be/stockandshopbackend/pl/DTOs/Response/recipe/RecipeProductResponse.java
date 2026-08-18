package be.stockandshopbackend.pl.DTOs.Response.recipe;

import be.stockandshopbackend.dl.entities.recipe.RecipeProduct;

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
                rProduct.getUnity() != null
                        ? rProduct.getUnity().name()
                        : rProduct.getProduct().getUnity().name()
        );
    }

}
