package be.stockandshopbackend.pl.DTOs.requests.recipe;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record RecipeCommentRequest(
        @NotBlank @Size(max = 2000) String comment,
        @Min(1) @Max(5) int score,
        @NotNull UUID recipeId
) {}
