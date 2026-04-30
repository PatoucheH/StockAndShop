package be.stockandshopbackend.pl.DTOs.requests;

import jakarta.validation.constraints.NotBlank;

public record HomeRequest(
        @NotBlank String name,
        String description
) {
}
