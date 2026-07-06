package be.stockandshopbackend.dal.repositories.recipe;

import be.stockandshopbackend.dl.entities.recipe.RecipeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {

    List<RecipeComment> findByRecipe_Id(UUID id);

    boolean existsByRecipe_IdAndUser_id(UUID recipeId, UUID userId);

    @Query("SELECT AVG(c.score) FROM RecipeComment c WHERE c.recipe.id = :recipeId")
    Double averageScoreByRecipeId(@Param("recipeId") UUID recipeId);

}
