package be.stockandshopbackend.bll.services.user.userService;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.user.UserRepository;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseCRUDService<User, UUID, UserRepository>
                            implements UserService {

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No user found with email: " + email));
    }

    @Override
    public List<User> searchByQuery(String query) {
        return repository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }
}
