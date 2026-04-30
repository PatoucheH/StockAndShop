package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.ShoppingList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShoppingListResponse {
    private Long id;
    private String name;
    private String description;


    public static ShoppingListResponse fromShoppingList(ShoppingList shoppingList) {
        return new ShoppingListResponse(
                shoppingList.getId(),
                shoppingList.getName(),
                shoppingList.getDescription()
        );
    }
}
