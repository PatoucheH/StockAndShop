package be.stockandshop.dal.entities;

import be.stockandshop.dal.entities.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class ProductStockLine extends BaseEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private Home home;

    private Integer quantity;
}
