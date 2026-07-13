package be.stockandshopbackend.pl.DTOs.requests.recipe;

import be.stockandshopbackend.pl.DTOs.requests.products.ProductItemRequest;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Plain DTO (name + quantity) instead of the ProductStockHome entity:
 * clients must never deserialize directly into JPA entities.
 * Products are resolved from the database in the controller.
 */
public record GenerateRecipeRequest(
        @Valid List<ProductItemRequest> products
) {
}
