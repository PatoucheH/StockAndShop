package be.stockandshopbackend.dl.entities.user;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper = true) @ToString
@NoArgsConstructor @AllArgsConstructor
public class Role extends LongBaseEntity implements GrantedAuthority {

    @Column(nullable = false, unique = true)
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
