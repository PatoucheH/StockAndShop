package be.stockandshopbackend.bll.services.recipe.recipeComment;

import be.stockandshopbackend.dal.repositories.recipe.RecipeCommentRepository;
import be.stockandshopbackend.dal.repositories.recipe.RecipeRepository;
import be.stockandshopbackend.dal.repositories.user.UserRepository;
import be.stockandshopbackend.dl.entities.recipe.Recipe;
import be.stockandshopbackend.dl.entities.recipe.RecipeComment;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.exceptions.NotFoundException;
import be.stockandshopbackend.pl.DTOs.requests.recipe.RecipeCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeCommentServiceImpl implements RecipeCommentService {

    private final RecipeCommentRepository recipeCommentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public List<RecipeComment> getRecipeCommentsByRecipeId(UUID recipeId) {
        return recipeCommentRepository.findByRecipe_Id(recipeId);
    }

    @Transactional
    public RecipeComment addRecipeComment(RecipeCommentRequest request, String username) {
        // username = Spring Security principal name = email in this app
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
        Recipe recipe = recipeRepository.findById(request.recipeId())
                .orElseThrow(() -> new NotFoundException("Recipe not found: " + request.recipeId()));

        if(recipeCommentRepository.existsByRecipe_IdAndUser_id(recipe.getId(), user.getId())){
            throw new IllegalStateException("Vous avez déjà noté cette recette");
        }
            RecipeComment comment = new RecipeComment();
        comment.setComment(request.comment());
        comment.setScore(request.score());
        comment.setRecipe(recipe);
        comment.setUser(user);

        RecipeComment saved = recipeCommentRepository.save(comment);
        recomputeScore(recipe);
        return saved;
    }

    @Transactional
    public void deleteRecipeComment(Long recipeCommentId, User currentUser) {
        RecipeComment comment = recipeCommentRepository.findById(recipeCommentId)
                .orElseThrow(() -> new NotFoundException("Comment not found: " + recipeCommentId));
        // Only the comment's author (or an admin) may delete it
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> "ADMIN".equals(a.getAuthority()));
        if (!isAdmin && !comment.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only delete your own comments");
        }
        Recipe recipe = comment.getRecipe();
        recipeCommentRepository.delete(comment);
        recomputeScore(recipe);
    }

    private void recomputeScore(Recipe recipe) {
        Double avg = recipeCommentRepository.averageScoreByRecipeId(recipe.getId());
        recipe.setScore(avg);  // null si plus aucun commentaire, c'est correct
        recipeRepository.save(recipe);
    }

}
