package be.stockandshop.pl.dto.reponses;

import be.stockandshop.dal.entities.User;
import be.stockandshop.bll.enums.Roles;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UserResponse {
    private final UUID id;
    private final String email;
    private final String username;
    private final Roles role;
    private final LocalDateTime createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
