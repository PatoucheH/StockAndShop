package be.stockandshopbackend.pl.DTOs.Response.user;

import be.stockandshopbackend.dl.entities.user.Role;

public record RoleResponse(String name) {
    public static RoleResponse fromRole(Role role) {
        return new RoleResponse(role.getName());
    }
}
