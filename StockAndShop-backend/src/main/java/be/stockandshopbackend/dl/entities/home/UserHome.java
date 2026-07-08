package be.stockandshopbackend.dl.entities.home;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.dl.enums.HomeRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class UserHome extends LongBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomeRole homeRole;

    @Column(nullable = false)
    private int balance;

    public void changeBalance(int amount) {
        this.balance += amount;
    }

}
