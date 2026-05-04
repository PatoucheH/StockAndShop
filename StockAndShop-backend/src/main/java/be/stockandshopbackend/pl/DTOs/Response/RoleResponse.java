package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RoleResponse {
    private String name;

    public static RoleResponse fromRole(Role role) {
        return new RoleResponse(role.getName());
    }
}
