package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {
    Long id;
    String name;
    String description;

    public static CategoryResponse fromCategory(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getDescription()
        );
    }
}
