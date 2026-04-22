package be.stockandshop.pl.dto.reponses;

import be.stockandshop.dal.entities.Home;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HomeResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public HomeResponse(Home home) {
        this.id = home.getId();
        this.name = home.getName();
        this.description = home.getDescription();
        this.createdAt = home.getCreatedAt();
        this.updatedAt = home.getUpdatedAt();
    }
}
