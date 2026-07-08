package be.stockandshopbackend.pl.DTOs.Response.home;

import be.stockandshopbackend.dl.entities.home.UserHome;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserHomeResponse(
        UUID id,
        String name,
        String email,
        String homeRole,
        Set<String> roles,
        int balance
) {
    public static UserHomeResponse fromUserHome(UserHome u) {
        return new UserHomeResponse(
                u.getUser().getId(),
                u.getUser().getDisplayName(),
                u.getUser().getUsername(),
                u.getHomeRole().toString(),
                u.getUser().getAuthorities().stream().map(Object::toString).collect(Collectors.toSet()),
                u.getBalance()
        );
    }
}
