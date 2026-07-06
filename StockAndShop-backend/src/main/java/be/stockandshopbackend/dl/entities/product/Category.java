package be.stockandshopbackend.dl.entities.product;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Category extends LongBaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    public Category(String name) {
        this.name = name;
    }

}
