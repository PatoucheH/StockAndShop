package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.Role;

public record RoleResponse(String name) {
    public static RoleResponse fromRole(Role role) {
        return new RoleResponse(role.getName());
    }
}
