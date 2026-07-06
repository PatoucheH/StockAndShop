package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.pl.DTOs.Response.products.ProductItemResponse;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record ShoppingListResponse(
        Long id,
        String name,
        String description,
        List<ProductItemResponse> products,
        UUID homeId
) {
    public static ShoppingListResponse fromShoppingList(ShoppingList shoppingList) {
        return new ShoppingListResponse(
                shoppingList.getId(),
                shoppingList.getName(),
                shoppingList.getDescription(),
                // Sorted by category so the frontend can render grouped rows without additional sorting
                shoppingList.getProducts().stream()
                        .map(ProductItemResponse::fromProductListItem)
                        .sorted(Comparator.comparing(ProductItemResponse::category))
                        .toList(),
                shoppingList.getHomeId()
        );
    }
}
