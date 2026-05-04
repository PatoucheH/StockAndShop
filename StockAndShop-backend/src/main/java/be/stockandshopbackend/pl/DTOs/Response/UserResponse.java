package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.Category;
import be.stockandshopbackend.dl.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    UUID id;
    String name;
    String email;
    Set<String> roles;

    public static UserResponse fromUser(User u) {
        return new UserResponse(
                u.getId(),
                u.getDisplayName(),
                u.getUsername(),
                u.getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet())
        );
    }
}
