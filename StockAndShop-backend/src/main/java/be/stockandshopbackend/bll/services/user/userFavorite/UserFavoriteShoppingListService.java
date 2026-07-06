package be.stockandshopbackend.bll.services.user.userFavorite;

import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.dl.entities.user.User;

import java.util.List;

public interface UserFavoriteShoppingListService {

    List<ShoppingList> getFavorites(User user);

    void addFavorite(Long shoppingListId, User user);

    void removeFavorite(Long shoppingListId, User user);
}
