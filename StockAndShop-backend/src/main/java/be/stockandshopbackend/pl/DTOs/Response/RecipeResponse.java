package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.recipe.Recipe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        UUID id,
        String title,
        List<RecipeProductResponse> ingredients,
        List<String> steps,
        int timing,
        Double score,
        List<String> tags,
        LocalDateTime createdAt
) {
    public static RecipeResponse fromRecipe(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getRecipeProducts().stream()
                        .map(RecipeProductResponse::fromRecipeProduct)
                        .toList(),
                recipe.getSteps(),
                recipe.getTiming(),
                recipe.getScore(),
                recipe.getTags().stream().map(tag -> tag.getName()).toList(),
                recipe.getCreatedAt()
        );
    }
}
