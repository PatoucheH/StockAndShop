package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.recipe.RecipeComment;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecipeCommentResponse(
    Long id,
    String comment,
    int score,
    UUID recipeId,
    String username,
    LocalDateTime createdAt
) {
    public static RecipeCommentResponse fromRecipeComment(RecipeComment recipeComment) {
        return new RecipeCommentResponse(
                recipeComment.getId(),
                recipeComment.getComment(),
                recipeComment.getScore(),
                recipeComment.getRecipe().getId(),
                recipeComment.getUser().getUsername(),
                recipeComment.getCreatedAt()
        );
    }
}
