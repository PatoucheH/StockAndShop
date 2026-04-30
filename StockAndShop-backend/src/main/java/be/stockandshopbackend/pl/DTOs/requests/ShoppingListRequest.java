package be.stockandshopbackend.pl.DTOs.requests;

import jakarta.validation.constraints.NotBlank;

public record ShoppingListRequest(
        @NotBlank String name,
        String description
) {

}
