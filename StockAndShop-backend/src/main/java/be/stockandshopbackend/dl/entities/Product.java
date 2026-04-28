package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.BaseEntity;
import be.stockandshopbackend.dl.enums.Unity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(callSuper = false) @ToString
public class Product extends BaseEntity<Long> {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unity unity;

}
