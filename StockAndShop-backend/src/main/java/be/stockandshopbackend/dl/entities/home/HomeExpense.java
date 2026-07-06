package be.stockandshopbackend.dl.entities.home;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class HomeExpense extends LongBaseEntity {

    @Column(nullable = false, length = 512)
    private String name;

    @Column(nullable = false)
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private UserHome payer;

    @ManyToMany
    @JoinTable(name = "home_expense_user",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "user_home_id")
    )
    private List<UserHome> usersConcerned;

}
