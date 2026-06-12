package be.stockandshopbackend.dl.entities;

import be.stockandshopbackend.dl.entities.base.UuidBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@ToString(exclude = "password")
@NoArgsConstructor @AllArgsConstructor
public class User extends UuidBaseEntity implements UserDetails {

    @Setter
    @Column(nullable = false)
    private String username;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter @Setter
    @Column(nullable = false)
    private String password;

    @Getter @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public String getDisplayName(){
        return this.username;
    }

    // Spring Security uses email as the unique principal — the 'username' field is the display name only
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    public void  removeRole(Role role){
        this.roles.remove(role);
    }
}
