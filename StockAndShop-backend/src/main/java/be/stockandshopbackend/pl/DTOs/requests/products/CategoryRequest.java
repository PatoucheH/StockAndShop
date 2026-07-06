package be.stockandshopbackend.pl.DTOs.requests.products;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String name,
        String description
) {
}
