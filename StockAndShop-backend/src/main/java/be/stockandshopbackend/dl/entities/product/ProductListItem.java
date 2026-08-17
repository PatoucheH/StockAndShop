package be.stockandshopbackend.dl.entities.product;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import be.stockandshopbackend.dl.enums.Unity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class ProductListItem extends LongBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    // DB-level cascade: item is deleted when the referenced product is deleted, bypassing JPA lifecycle hooks
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean isChecked;

    // Unit chosen for this list line; null = fall back to the product's default unit
    @Enumerated(EnumType.STRING)
    private Unity unity;

    public ProductListItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.isChecked = false;
    }

    public ProductListItem(Product product, int quantity, Unity unity) {
        this.product = product;
        this.quantity = quantity;
        this.unity = unity;
        this.isChecked = false;
    }

    public void toggleIsChecked() {
        this.isChecked = !this.isChecked;
    }

}
