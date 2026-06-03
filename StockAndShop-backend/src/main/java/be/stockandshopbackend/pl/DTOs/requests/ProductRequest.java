package be.stockandshopbackend.pl.DTOs.requests;

import jakarta.validation.constraints.NotBlank;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String unity,
        @NotBlank String category
) {}
