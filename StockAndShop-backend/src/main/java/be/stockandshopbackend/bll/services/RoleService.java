package be.stockandshopbackend.bll.services;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.RoleRepository;
import be.stockandshopbackend.dal.repositories.UserRepository;
import be.stockandshopbackend.dl.entities.Role;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleService extends BaseCRUDService<Role, Long, RoleRepository> {

    private final UserRepository userRepository;

    protected RoleService(RoleRepository repository, UserRepository userRepository) {
        super(repository);
        this.userRepository = userRepository;
    }

    //region GET

    public Role createRole(String roleName){
        Role role = new Role(roleName);
        return repository.save(role);
    }

    //endregion

    //region ADD / REMOVE roles

    @Transactional
    public void addRoleToUser(UUID userId, String nameRole) {
        Role role = repository.findByName(nameRole).orElseThrow(() -> new NotFoundException("Role not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.addRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void removeRoleToUser(UUID userId, String nameRole) {
        Role role = repository.findByName(nameRole).orElseThrow(() -> new NotFoundException("Role not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.removeRole(role);
        userRepository.save(user);
    }

    //endregion

}
