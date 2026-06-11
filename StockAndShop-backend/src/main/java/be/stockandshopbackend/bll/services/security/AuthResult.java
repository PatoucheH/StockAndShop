package be.stockandshopbackend.bll.services.security;

import java.util.List;

public record AuthResult(
        String accessToken,
        String refreshToken,
        String email,
        String displayName,
        List<String> roles
) {
}
