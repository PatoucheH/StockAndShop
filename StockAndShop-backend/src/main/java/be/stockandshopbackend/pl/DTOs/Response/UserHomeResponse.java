package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.entities.UserHome;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserHomeResponse {
    String name;
    String email;
    String homeRole;
    Set<String> roles;

    public static UserHomeResponse fromUserHome(UserHome u) {
        return new UserHomeResponse(
                u.getUser().getDisplayName(),
                u.getUser().getUsername(),
                u.getHomeRole().toString(),
                u.getUser().getAuthorities().stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet())
        );
    }
}
