package be.stockandshopbackend.bll.services.productAndShoppingList.shoppingList;

import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.product.ProductListItem;
import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.dl.entities.user.User;

import java.util.List;
import java.util.UUID;

public interface ShoppingListService {

    /// GET
    ShoppingList findById(Long id);
    List<ShoppingList> findByUser(User userDetails);

    /// ADD
    Home createShoppingList(UUID homeId, String name, String description);
    void addProductToList(Long shoppingListId, String productName, int quantity);
    void addListProductsToList(Long shoppingListId, List<ProductListItem> products);


    /// DELETE CHECK ADD STOCK
    ShoppingList deleteProductCheckedAndAddStock(Long shoppingListId, UUID homeId);


    /// DELETE
    void deleteById(Long id);
    void removeProductFromList(Long shoppingListId, int productId);
}
