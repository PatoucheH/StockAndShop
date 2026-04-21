package be.stockandshop.entities;

import be.stockandshop.entities.base.BaseEntity;
import be.stockandshop.enums.HomeRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserHome extends BaseEntity<Long> {

    @ManyToOne
    private User user;

    @ManyToOne
    private Home home;

    @Enumerated(EnumType.STRING)
    private HomeRole role;

}
