package be.stockandshopbackend.pl.DTOs.websocket;

public record ShoppingListEventDTO(
        ShoppingListEventType type,
        Long shoppingListId,
        String triggeredByUsername,
        Object payload
) {}
