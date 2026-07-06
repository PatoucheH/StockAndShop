package be.stockandshopbackend.pl.DTOs.requests.home;

import be.stockandshopbackend.dl.enums.HomeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddUserToHomeRequest(
        @NotBlank @Email String email,
        HomeRole role
) {}
