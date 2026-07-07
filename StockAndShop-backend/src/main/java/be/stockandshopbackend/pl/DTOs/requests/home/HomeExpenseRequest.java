package be.stockandshopbackend.pl.DTOs.requests.home;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record HomeExpenseRequest(
        @NotBlank String name,
        @Min(0) int amount,
        @NotNull UUID homeId,
        @NotNull UUID payerId,
        @NotEmpty List<UUID> userConcernedId
) {}
