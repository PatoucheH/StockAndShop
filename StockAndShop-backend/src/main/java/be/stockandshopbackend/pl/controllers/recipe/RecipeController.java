package be.stockandshopbackend.pl.controllers.recipe;

import be.stockandshopbackend.bll.services.productAndShoppingList.product.ProductService;
import be.stockandshopbackend.bll.services.recipe.RecipeService;
import be.stockandshopbackend.bll.services.security.services.PremiumSecurityService;
import be.stockandshopbackend.dl.entities.product.ProductStockHome;
import be.stockandshopbackend.dl.entities.recipe.Recipe;
import be.stockandshopbackend.pl.DTOs.Response.recipe.PagedRecipeResponse;
import be.stockandshopbackend.pl.DTOs.Response.recipe.RecipeResponse;
import be.stockandshopbackend.pl.DTOs.requests.recipe.GenerateRecipeRequest;
import be.stockandshopbackend.pl.DTOs.requests.recipe.SuggestionsWithProductsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final ProductService productService;
    private final PremiumSecurityService premiumSecurity;

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        return ResponseEntity.ok(recipeService.getAllTags());
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(RecipeResponse.fromRecipe(recipeService.getById(id)));
    }

    @GetMapping("/{id}/suggestions")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<RecipeResponse>> getSuggestions(
            @PathVariable UUID id, Authentication authentication) {
        premiumSecurity.require(authentication);
        return ResponseEntity.ok(
                recipeService.getSuggestions(id).stream()
                        .map(RecipeResponse::fromRecipe)
                        .toList()
        );
    }

    @PostMapping("/{id}/suggestions")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<RecipeResponse>> getSuggestionsWithProducts(
            @PathVariable UUID id,
            @RequestBody SuggestionsWithProductsRequest request,
            Authentication authentication) {
        premiumSecurity.require(authentication);
        return ResponseEntity.ok(
                recipeService.getSuggestionsWithProducts(id, request.productNames()).stream()
                        .map(RecipeResponse::fromRecipe)
                        .toList()
        );
    }

    @PostMapping("/{id}/generate")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<RecipeResponse> generateRecipe(
            @PathVariable UUID id,
            @RequestBody(required = false) @Valid GenerateRecipeRequest request,
            Authentication authentication) {
        premiumSecurity.require(authentication,
                "La génération de recettes par IA est réservée aux membres premium.");

        Recipe recipe;
        if (request != null
                && request.products() != null
                && !request.products().isEmpty()) {
            // Resolve real products from the DB — never trust client-provided entities
            List<ProductStockHome> products = request.products().stream()
                    .map(p -> new ProductStockHome(
                            productService.findOneByName(p.name()),
                            p.quantity()))
                    .toList();
            recipe = recipeService.generateAndSaveWithProduct(products);
        } else {
            recipe = recipeService.generateAndSave(id);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RecipeResponse.fromRecipe(recipe));
    }
}
