package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor @AllArgsConstructor
public class ProductListLine extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer quantity;

}
