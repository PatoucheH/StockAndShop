package be.stockandshop.dal.entities;

import be.stockandshop.dal.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    private List<ProductStockLine> stock = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "home_id")
    private List<ShoppingList> shoppingLists = new ArrayList<>();

    @OneToMany(mappedBy = "home")
    private List<UserHome> userHomes  = new ArrayList<>();

    public Home(String name){
        this.name = name;
    }

    public Home(String name, String description) {
        this(name);
        this.description = description;
    }
}
