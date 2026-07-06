package be.stockandshopbackend.pl.DTOs.requests.products;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/// DTO for ProductStockHome AND ProductListItem creation

public record ProductItemRequest(
        @NotBlank String name,
        @Min(1) int quantity
) {
}
