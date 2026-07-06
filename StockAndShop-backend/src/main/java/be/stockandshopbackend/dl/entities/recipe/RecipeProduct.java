package be.stockandshopbackend.dl.entities.recipe;

import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RecipeProduct extends LongBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;
}
