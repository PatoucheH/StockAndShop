package be.stockandshopbackend.bll.services.userFavorite;

import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.dl.entities.User;

import java.util.List;

public interface UserFavoriteShoppingListService {

    List<ShoppingList> getFavorites(User user);

    void addFavorite(Long shoppingListId, User user);

    void removeFavorite(Long shoppingListId, User user);
}
