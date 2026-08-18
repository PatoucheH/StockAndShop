package be.stockandshopbackend.dl.entities.product;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import be.stockandshopbackend.dl.enums.Unity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor
public class ProductStockHome extends LongBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    // DB-level cascade: item is deleted when the referenced product is deleted, bypassing JPA lifecycle hooks
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    // Unit chosen for this stock line; null = fall back to the product's default unit
    @Enumerated(EnumType.STRING)
    private Unity unity;

    public ProductStockHome(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductStockHome(Product product, int quantity, Unity unity) {
        this.product = product;
        this.quantity = quantity;
        this.unity = unity;
    }

    // The unit actually shown for this line: the chosen unit, or the product's default when none was chosen.
    // Used for merging so a null line and a "default-valued" line are treated as the same unit.
    public Unity getEffectiveUnity() {
        return unity != null ? unity : (product != null ? product.getUnity() : null);
    }

}
