package be.stockandshopbackend.pl.DTOs.requests.products;

import jakarta.validation.constraints.NotBlank;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String unity,
        @NotBlank String category
) {}
