package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.Home;
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


    public static HomeResponse fromHome(Home h) {
        return new HomeResponse(
                h.getId(),
                h.getName(),
                h.getDescription()
        );
    }
}