package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
public class ProductStockHome extends LongBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    // DB-level cascade: item is deleted when the referenced product is deleted, bypassing JPA lifecycle hooks
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @Column(nullable = false)
    private int quantity;

}
