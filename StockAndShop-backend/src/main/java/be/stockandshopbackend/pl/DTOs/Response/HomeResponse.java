package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.Home;
import be.stockandshopbackend.dl.enums.HomeRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HomeResponse {
    private UUID id;
    private String name;
    private String description;
    private String ownerEmail;


    public static HomeResponse fromHome(Home h) {
        String ownerEmail = h.getUsers().stream()
                .filter(uh -> uh.getHomeRole() == HomeRole.OWNER)
                .map(uh -> uh.getUser().getUsername())
                .findFirst()
                .orElse(null);
        return new HomeResponse(
                h.getId(),
                h.getName(),
                h.getDescription(),
                ownerEmail
        );
    }
}