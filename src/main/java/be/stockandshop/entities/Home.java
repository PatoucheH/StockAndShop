package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Home extends BaseEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductStockLine> stock;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "home_id")
    private List<ShoppingList> shoppingLists;

    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserHome> userHomes;
}
