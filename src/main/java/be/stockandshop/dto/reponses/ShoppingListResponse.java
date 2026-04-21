package be.stockandshop.dto.reponses;

import be.stockandshop.entities.ShoppingList;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ShoppingListResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ShoppingListResponse(ShoppingList shoppingList) {
        this.id = shoppingList.getId();
        this.name = shoppingList.getName();
        this.description = shoppingList.getDescription();
        this.createdAt = shoppingList.getCreatedAt();
        this.updatedAt = shoppingList.getUpdatedAt();
    }
}
