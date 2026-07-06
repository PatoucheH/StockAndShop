package be.stockandshopbackend.bll.services.user.role;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.user.RoleRepository;
import be.stockandshopbackend.dal.repositories.user.UserRepository;
import be.stockandshopbackend.dl.entities.user.Role;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleServiceImpl extends BaseCRUDService<Role, Long, RoleRepository>
                            implements RoleService {

    private final UserRepository userRepository;

    protected RoleServiceImpl(RoleRepository repository, UserRepository userRepository) {
        super(repository);
        this.userRepository = userRepository;
    }

    //region GET


    //endregion

    //region ADD / REMOVE roles

    public Role createRole(String roleName){
        Role role = new Role(roleName);
        return repository.save(role);
    }

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
