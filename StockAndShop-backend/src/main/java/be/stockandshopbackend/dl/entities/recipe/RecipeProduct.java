package be.stockandshopbackend.dl.entities.recipe;

import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import be.stockandshopbackend.dl.enums.Unity;
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

    // Unit of this ingredient (taken from the stock line at generation time); null = product default
    @Enumerated(EnumType.STRING)
    private Unity unity;

    public RecipeProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
