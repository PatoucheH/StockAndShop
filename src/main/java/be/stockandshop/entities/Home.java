package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Home extends BaseEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @OneToMany(mappedBy = "home")
    private List<ProductStockLine> stock;

    @OneToMany
    @JoinColumn(name = "home_id")
    private List<ShoppingList> shoppingLists;

    @OneToMany(mappedBy = "home")
    private List<UserHome> userHomes;
}
