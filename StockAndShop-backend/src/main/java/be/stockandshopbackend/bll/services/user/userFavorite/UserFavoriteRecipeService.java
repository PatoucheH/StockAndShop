package be.stockandshopbackend.bll.services.user.userFavorite;

import be.stockandshopbackend.dl.entities.recipe.Recipe;
import be.stockandshopbackend.dl.entities.user.User;

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
