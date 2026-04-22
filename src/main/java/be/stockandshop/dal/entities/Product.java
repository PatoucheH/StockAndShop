package be.stockandshop.dal.entities;

import be.stockandshop.dal.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = false)
public class Product extends BaseEntity<Long> {

    @Getter @Setter
    @Column(nullable = false, length = 100)
    private String name;

    public Product(String name) {
        this.name = name;
    }
}
