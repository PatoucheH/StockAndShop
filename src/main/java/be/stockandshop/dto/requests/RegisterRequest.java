package be.stockandshop.dto.requests;

import be.stockandshop.entities.User;
import be.stockandshop.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 8)
    private String password;
    private String username;

    public User toEntity(String encodedPassword) {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(encodedPassword);
        user.setUsername(this.username);
        user.setRole(Roles.USER);
        return user;
    }
}
