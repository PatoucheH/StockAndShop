package be.stockandshopbackend.pl.controllers.recipe;

import be.stockandshopbackend.bll.services.recipe.recipeComment.RecipeCommentService;
import be.stockandshopbackend.pl.DTOs.Response.recipe.RecipeCommentResponse;
import be.stockandshopbackend.pl.DTOs.requests.recipe.RecipeCommentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe-comment")
public class RecipeCommentController {

    private final RecipeCommentService recipeCommentService;

    @GetMapping("/recipe/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RecipeCommentResponse>> findByRecipe_Id(@PathVariable UUID id) {
        return ResponseEntity.ok(
                recipeCommentService.getRecipeCommentsByRecipeId(id).stream()
                        .map(RecipeCommentResponse::fromRecipeComment)
                        .toList()
        );
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipeCommentResponse> addRecipeComment(
            @RequestBody @Valid RecipeCommentRequest recipeCommentRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                RecipeCommentResponse.fromRecipeComment(
                        recipeCommentService.addRecipeComment(recipeCommentRequest, userDetails.getUsername()))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteRecipeComment(@PathVariable Long recipeCommentId) {
        recipeCommentService.deleteRecipeComment(recipeCommentId);
        return ResponseEntity.ok().build();
    }
}
