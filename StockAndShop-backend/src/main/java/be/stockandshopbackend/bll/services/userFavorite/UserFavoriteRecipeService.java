package be.stockandshopbackend.bll.services.userFavorite;

import be.stockandshopbackend.dl.entities.Recipe;
import be.stockandshopbackend.dl.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserFavoriteRecipeService {

    /// GET
    List<Recipe> getFavorites(User user);

    /// ADD
    void addFavorite(UUID recipeId, User user);

    /// DELETE
    void removeFavorite(UUID recipeId, User user);
}
