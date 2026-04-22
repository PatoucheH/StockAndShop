package be.stockandshop.bll.services.interfaces.shoppingList;

import be.stockandshop.pl.dto.reponses.ShoppingListResponse;
import be.stockandshop.pl.dto.requests.ShoppingListRequest;

public interface ShoppingListCommandService {

    ShoppingListResponse save(ShoppingListRequest shoppingList);
    void deleteShoppingListById(Long id);
    ShoppingListResponse addProduct(Long productId, Long shoppingListId, Integer quantity);
    ShoppingListResponse removeProduct(Long productListLineId, Long shoppingListId);
}
