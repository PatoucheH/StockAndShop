package be.stockandshopbackend.dal.repositories.user;

import be.stockandshopbackend.dl.entities.recipe.Recipe;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.dl.entities.user.UserFavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteRecipeRepository extends JpaRepository<UserFavoriteRecipe, Long> {
    List<UserFavoriteRecipe> findByUser(User user);
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    void deleteByUserAndRecipe(User user, Recipe recipe);
    void deleteByUser(User user);
}
