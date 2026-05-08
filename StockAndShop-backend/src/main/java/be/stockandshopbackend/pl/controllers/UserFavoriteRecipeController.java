package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.userFavorite.UserFavoriteRecipeService;
import be.stockandshopbackend.bll.services.userFavorite.UserFavoriteRecipeServiceImpl;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.pl.DTOs.Response.RecipeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class UserFavoriteRecipeController {

    private final UserFavoriteRecipeService favoriteService;

    @GetMapping("/favorites")
    public ResponseEntity<List<RecipeResponse>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                favoriteService.getFavorites((User) userDetails).stream()
                        .map(RecipeResponse::fromRecipe)
                        .toList()
        );
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> addFavorite(@PathVariable UUID id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.addFavorite(id, (User) userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> removeFavorite(@PathVariable UUID id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.removeFavorite(id, (User) userDetails);
        return ResponseEntity.noContent().build();
    }
}
