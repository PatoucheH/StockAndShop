package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ShoppingList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShoppingListResponse {
    private Long id;
    private String name;
    private String description;
    private List<ProductItemResponse> products;


    public static ShoppingListResponse fromShoppingList(ShoppingList shoppingList) {
        return new ShoppingListResponse(
                shoppingList.getId(),
                shoppingList.getName(),
                shoppingList.getDescription(),
                shoppingList.getProducts().stream()
                        .map(ProductItemResponse::fromProductListItem)
                        .sorted(Comparator.comparing(ProductItemResponse::getCategory))
                        .toList()
        );
    }
}
