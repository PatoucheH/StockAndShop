package be.stockandshopbackend.bll.services.shoppingList;

import be.stockandshopbackend.dl.entities.Home;
import be.stockandshopbackend.dl.entities.ShoppingList;

import java.util.UUID;

public interface ShoppingListService {

    /// GET
    ShoppingList findById(Long id);

    /// ADD
    Home createShoppingList(UUID homeId, String name, String description);
    void addProductFromList(Long shoppingListId, String productName, int quantity);


    /// DELETE CHECK ADD STOCK
    ShoppingList deleteProductCheckedAndAddStock(Long shoppingListId, UUID homeId);


    /// DELETE
    void deleteById(Long id);
    void removeProductFromList(Long shoppingListId, int productId);
}
