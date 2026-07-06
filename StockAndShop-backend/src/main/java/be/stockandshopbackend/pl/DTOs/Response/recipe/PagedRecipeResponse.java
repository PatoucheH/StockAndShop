package be.stockandshopbackend.pl.DTOs.Response.recipe;

import java.util.List;

public record PagedRecipeResponse(
        List<RecipeResponse> recipes,
        long total,
        int page,
        int size,
        boolean hasMore
) {}
