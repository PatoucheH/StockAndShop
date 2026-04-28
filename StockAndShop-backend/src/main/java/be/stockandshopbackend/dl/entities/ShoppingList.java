package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode @ToString
public class ShoppingList extends BaseEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    


}
