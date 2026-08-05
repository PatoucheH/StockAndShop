package be.stockandshopbackend.pl.DTOs.requests.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUsernameRequest(
        @NotBlank(message = "Le nom d'utilisateur est requis")
        @Size(max = 50, message = "Le nom d'utilisateur ne peut pas dépasser 50 caractères")
        String username
) {
}
