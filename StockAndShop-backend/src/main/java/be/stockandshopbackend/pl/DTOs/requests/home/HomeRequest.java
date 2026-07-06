package be.stockandshopbackend.pl.DTOs.requests.home;

import jakarta.validation.constraints.NotBlank;

public record HomeRequest(
        @NotBlank String name,
        String description
) {}
