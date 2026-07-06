package be.stockandshopbackend.dl.entities.recipe;

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
public class Tag extends LongBaseEntity {

    @Column(unique = true, nullable = false)
    private String name;
}
