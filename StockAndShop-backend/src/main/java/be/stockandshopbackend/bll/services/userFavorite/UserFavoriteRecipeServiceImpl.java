package be.stockandshopbackend.bll.services.userFavorite;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.RecipeRepository;
import be.stockandshopbackend.dal.repositories.UserFavoriteRecipeRepository;
import be.stockandshopbackend.dl.entities.Recipe;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.entities.UserFavoriteRecipe;
import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserFavoriteRecipeServiceImpl
        extends BaseCRUDService<UserFavoriteRecipe, Long, UserFavoriteRecipeRepository>
        implements UserFavoriteRecipeService {

    private final RecipeRepository recipeRepository;

    public UserFavoriteRecipeServiceImpl(UserFavoriteRecipeRepository favoriteRepository,
                                         RecipeRepository recipeRepository) {
        super(favoriteRepository);
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getFavorites(User user) {
        return repository.findByUser(user).stream()
                .map(UserFavoriteRecipe::getRecipe)
                .toList();
    }

    @Transactional
    public void addFavorite(UUID recipeId, User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new NotFoundException("Recipe not found with id: " + recipeId)
        );
        if (repository.existsByUserAndRecipe(user, recipe)) {
            throw new IllegalStateException("Recipe already in favorites");
        }
        save(new UserFavoriteRecipe(user, recipe));
    }

    @Transactional
    public void removeFavorite(UUID recipeId, User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new NotFoundException("Recipe not found with id: " + recipeId)
        );
        repository.deleteByUserAndRecipe(user, recipe);
    }
}
