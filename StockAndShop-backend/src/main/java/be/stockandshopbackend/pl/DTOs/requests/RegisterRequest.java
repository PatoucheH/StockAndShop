package be.stockandshopbackend.pl.DTOs.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank @Min(8)  String password
) {
}
