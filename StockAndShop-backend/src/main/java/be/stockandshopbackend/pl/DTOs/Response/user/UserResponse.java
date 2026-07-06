package be.stockandshopbackend.pl.DTOs.Response.user;

import be.stockandshopbackend.dl.entities.user.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Set<String> roles
) {
    public static UserResponse fromUser(User u) {
        return new UserResponse(
                u.getId(),
                u.getDisplayName(),
                u.getUsername(),
                u.getAuthorities().stream().map(Object::toString).collect(Collectors.toSet())
        );
    }
}
