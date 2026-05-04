package be.stockandshopbackend.pl.DTOs.requests;

public record LoginRequest(
        String email,
        String password
) {
}
