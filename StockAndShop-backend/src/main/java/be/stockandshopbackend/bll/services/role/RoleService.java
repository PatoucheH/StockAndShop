package be.stockandshopbackend.bll.services.role;

import be.stockandshopbackend.dl.entities.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    /// GET
    List<Role> findAll();


    /// ADD
    Role createRole(String roleName);
    void addRoleToUser(UUID userId, String nameRole);

    /// DELETE
    void removeRoleToUser(UUID userId, String nameRole);
}
