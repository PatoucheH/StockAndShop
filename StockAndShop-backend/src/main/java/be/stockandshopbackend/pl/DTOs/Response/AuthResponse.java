package be.stockandshopbackend.pl.DTOs.Response;

import java.util.List;

public record AuthResponse(
        String token,
        String email,
        String displayName,
        List<String> roles
) {
}
