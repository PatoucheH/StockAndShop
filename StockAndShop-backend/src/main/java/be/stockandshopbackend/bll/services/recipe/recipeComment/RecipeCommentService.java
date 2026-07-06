package be.stockandshopbackend.bll.services.recipe.recipeComment;

import be.stockandshopbackend.dl.entities.recipe.RecipeComment;
import be.stockandshopbackend.pl.DTOs.requests.recipe.RecipeCommentRequest;

import java.util.List;
import java.util.UUID;

public interface RecipeCommentService {

    List<RecipeComment> getRecipeCommentsByRecipeId(UUID recipeId);

    RecipeComment addRecipeComment(RecipeCommentRequest request, String username);

    void deleteRecipeComment(Long recipeCommentId);

}
