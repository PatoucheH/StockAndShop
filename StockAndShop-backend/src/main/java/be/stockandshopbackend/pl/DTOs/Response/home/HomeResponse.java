package be.stockandshopbackend.pl.DTOs.Response.home;

import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.enums.HomeRole;

import java.util.UUID;

public record HomeResponse(
        UUID id,
        String name,
        String description,
        String ownerEmail
) {
    public static HomeResponse fromHome(Home h) {
        // getUsername() returns the email field (Spring Security principal) — not the display name
        String ownerEmail = h.getUsers().stream()
                .filter(uh -> uh.getHomeRole() == HomeRole.OWNER)
                .map(uh -> uh.getUser().getUsername())
                .findFirst()
                .orElse(null);
        return new HomeResponse(h.getId(), h.getName(), h.getDescription(), ownerEmail);
    }
}
