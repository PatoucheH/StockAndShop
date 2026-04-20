package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import be.stockandshop.enums.Roles;
import jakarta.persistence.*;

@Entity
public class UserHome extends BaseEntity<Long> {

    @ManyToOne
    private User user;

    @ManyToOne
    private Home home;

    @Enumerated(EnumType.STRING)
    private Roles role;

}
