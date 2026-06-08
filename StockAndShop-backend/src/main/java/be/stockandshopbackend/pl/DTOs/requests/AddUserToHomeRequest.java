package be.stockandshopbackend.pl.DTOs.requests;

import be.stockandshopbackend.dl.enums.HomeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddUserToHomeRequest(
        @NotBlank @Email String email,
        HomeRole role
) {}
