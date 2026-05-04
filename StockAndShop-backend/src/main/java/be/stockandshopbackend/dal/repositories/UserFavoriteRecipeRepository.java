package be.stockandshopbackend.dal.repositories;

import be.stockandshopbackend.dl.entities.Recipe;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.entities.UserFavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserFavoriteRecipeRepository extends JpaRepository<UserFavoriteRecipe, Long> {
    List<UserFavoriteRecipe> findByUser(User user);
    boolean existsByUserAndRecipe(User user, Recipe recipe);
    void deleteByUserAndRecipe(User user, Recipe recipe);
}
