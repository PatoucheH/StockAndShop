package be.stockandshopbackend.dl.entities.home;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import be.stockandshopbackend.dl.enums.ExpenseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter @Setter
public class HomeExpense extends LongBaseEntity {

    @Column(nullable = false, length = 512)
    private String name;

    @Column(nullable = false)
    private int amount;

    // Distinguishes a real expense (split between members) from a reimbursement recorded for history
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'EXPENSE'")
    private ExpenseType type = ExpenseType.EXPENSE;

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

    // Existing 5-arg constructor kept so the create-expense flow is unchanged (type defaults to EXPENSE)
    public HomeExpense(String name, int amount, Home home, UserHome payer, List<UserHome> usersConcerned) {
        this.name = name;
        this.amount = amount;
        this.home = home;
        this.payer = payer;
        this.usersConcerned = usersConcerned;
        this.type = ExpenseType.EXPENSE;
    }

}
