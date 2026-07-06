package be.stockandshopbackend.dl.entities.product;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
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

    public ProductListItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.isChecked = false;
    }

    public void toggleIsChecked() {
        this.isChecked = !this.isChecked;
    }

}
