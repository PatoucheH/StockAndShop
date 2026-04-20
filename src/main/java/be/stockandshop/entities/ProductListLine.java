package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class ProductListLine extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Integer quantity;
}
