package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class ProductStockLine extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer quantity;
}
