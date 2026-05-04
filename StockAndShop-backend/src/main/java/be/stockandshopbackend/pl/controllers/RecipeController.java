package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.RecipeService;
import be.stockandshopbackend.dl.entities.Recipe;
import be.stockandshopbackend.pl.DTOs.Response.RecipeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/{id}/recipe")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<RecipeResponse>> getRecipes(@PathVariable UUID id) {
        return ResponseEntity.ok(
                recipeService.findAllByHomeId(id).stream()
                        .map(RecipeResponse::fromRecipe)
                        .toList()
        );
    }

    @PostMapping("/{id}/recipe/generate")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<RecipeResponse> generateRecipe(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RecipeResponse.fromRecipe(recipeService.generateAndSave(id)));
    }
}
