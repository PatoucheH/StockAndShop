package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.product.Category;

public record CategoryResponse(
        Long id,
        String name,
        String description
) {
    public static CategoryResponse fromCategory(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}
