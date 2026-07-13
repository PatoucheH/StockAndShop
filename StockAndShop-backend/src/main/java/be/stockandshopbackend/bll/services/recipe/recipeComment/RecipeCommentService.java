package be.stockandshopbackend.bll.services.recipe.recipeComment;

import be.stockandshopbackend.dl.entities.recipe.RecipeComment;
import be.stockandshopbackend.dl.entities.user.User;

import java.util.List;
import java.util.UUID;

public interface RecipeCommentService {

    List<RecipeComment> getRecipeCommentsByRecipeId(UUID recipeId);

    RecipeComment addRecipeComment(UUID recipeId, String comment, int score, String username);

    void deleteRecipeComment(Long recipeCommentId, User currentUser);

}
