package be.stockandshopbackend.bll.services.recipe;

import be.stockandshopbackend.dl.entities.Recipe;

import java.util.List;
import java.util.UUID;

public interface RecipeService {

    /// GET
    List<Recipe> getAllRecipes();
    List<Recipe> getSuggestions(UUID homeId);

    /// ADD / GENERATE
    Recipe generateAndSave(UUID homeId);


}
