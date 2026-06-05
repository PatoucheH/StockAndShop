package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.recipe.RecipeService;
import be.stockandshopbackend.dl.entities.Recipe;
import be.stockandshopbackend.pl.DTOs.Response.PagedRecipeResponse;
import be.stockandshopbackend.pl.DTOs.Response.RecipeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<PagedRecipeResponse> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Page<Recipe> recipePage = recipeService.getAllRecipes(PageRequest.of(page, size));
        List<RecipeResponse> recipes = recipePage.getContent().stream()
                .map(RecipeResponse::fromRecipe)
                .toList();
        return ResponseEntity.ok(new PagedRecipeResponse(
                recipes,
                recipePage.getTotalElements(),
                page,
                size,
                recipePage.hasNext()
        ));
    }

    @GetMapping("/{id}/suggestions")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<RecipeResponse>> getSuggestions(@PathVariable UUID id) {
        return ResponseEntity.ok(
                recipeService.getSuggestions(id).stream()
                        .map(RecipeResponse::fromRecipe)
                        .toList()
        );
    }

    @PostMapping("/{id}/generate")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<RecipeResponse> generateRecipe(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RecipeResponse.fromRecipe(recipeService.generateAndSave(id)));
    }
}
