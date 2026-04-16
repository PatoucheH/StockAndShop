package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
public class ShoppingList extends BaseEntity<Long>{

    @Getter @Setter
    @Column(length = 100, nullable = false)
    private String name;

    @Getter @Setter
    @Column(length = 500, nullable = true)
    private String description;

    @OneToMany
    @Getter
    @JoinColumn(name = "shopping_list_id")
    private List<Product> products;

}
