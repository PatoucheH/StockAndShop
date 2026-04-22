package be.stockandshop.bll.services.interfaces.shoppingList;

import be.stockandshop.pl.dto.reponses.ShoppingListResponse;

public interface ShoppingListQueryService {
    ShoppingListResponse getShoppingListById(Long id);
}
