package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.UuidBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
public class Home extends UuidBaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @OneToMany(cascade =  CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "home_id", nullable = false)
    private List<ProductStockHome> stocks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "home_id", nullable = false)
    private List<ShoppingList> shoppingLists = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "home_id", nullable = false)
    private List<UserHome> users = new ArrayList<>();


    public Home(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addShoppingList(ShoppingList shoppingList) {
        shoppingLists.add(shoppingList);
    }

    public void addProductStock(ProductStockHome productStockHome) {
        stocks.stream()
        .filter(s -> s.getProduct().getId().equals(productStockHome.getProduct().getId()))
        .findFirst()
        .ifPresentOrElse(
                existing -> existing.setQuantity(existing.getQuantity() + productStockHome.getQuantity()),
                () -> stocks.add(productStockHome)
        );
    }

    public void addUserHome(UserHome userHome) {
        users.add(userHome);
    }
}
