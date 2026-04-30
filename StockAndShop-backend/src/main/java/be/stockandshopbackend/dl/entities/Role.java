package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper = true) @ToString
@NoArgsConstructor @AllArgsConstructor
public class Role extends LongBaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
