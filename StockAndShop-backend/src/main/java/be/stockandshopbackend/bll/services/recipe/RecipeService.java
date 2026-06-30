package be.stockandshopbackend.bll.services.recipe;

import be.stockandshopbackend.dl.entities.ProductStockHome;
import be.stockandshopbackend.dl.entities.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RecipeService {

    /// GET
    Page<Recipe> getAllRecipes(Pageable pageable);
    Recipe getById(UUID id);
    List<Recipe> getSuggestions(UUID homeId);
    List<Recipe> getSuggestionsWithProducts(UUID homeId, List<String> productNames);
    List<String> getAllTags();

    /// ADD / GENERATE
    Recipe generateAndSaveWithProduct(List<ProductStockHome> product);
    Recipe generateAndSave(UUID homeId);


}
