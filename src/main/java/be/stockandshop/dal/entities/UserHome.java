package be.stockandshop.dal.entities;

import be.stockandshop.dal.entities.base.BaseEntity;
import be.stockandshop.bll.enums.HomeRole;
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
