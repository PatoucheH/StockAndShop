package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
public class ShoppingList extends LongBaseEntity {

    // Read-only mirror of the FK column written by Home's @JoinColumn; insertable/updatable=false
    // prevents JPA from treating it as a second managed column for the same FK.
    @Column(name = "home_id", insertable = false, updatable = false)
    private UUID homeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private List<ProductListItem> products = new ArrayList<>();

    public ShoppingList(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Accumulates quantity if the product already exists in the list, otherwise adds a new entry
    public void addProduct(ProductListItem item) {
        products.stream()
                .filter(p -> p.getProduct().getId().equals(item.getProduct().getId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + item.getQuantity()),
                        () -> products.add(item)
                );
    }


}
