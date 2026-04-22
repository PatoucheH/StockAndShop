package be.stockandshop.dal.entities;

import be.stockandshop.dal.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class ShoppingList extends BaseEntity<Long>{

    @Getter @Setter
    @Column(length = 100, nullable = false)
    private String name;

    @Getter @Setter
    @Column(length = 500, nullable = true)
    private String description;

    @Getter
    @OneToMany
    @JoinColumn(name = "shopping_list_id")
    private List<ProductListLine> products = new ArrayList<>();

    public ShoppingList(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public ShoppingList(String name, String description) {
        this();
        this.description = description;
    }

    public void addProduct(ProductListLine product) {
        this.products.add(product);
    }

    public void removeProduct(ProductListLine product) {
        this.products.remove(product);
    }

}
