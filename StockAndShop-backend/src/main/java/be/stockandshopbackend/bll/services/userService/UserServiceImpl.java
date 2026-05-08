package be.stockandshopbackend.bll.services.userService;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.UserRepository;
import be.stockandshopbackend.dl.entities.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl extends BaseCRUDService<User, UUID, UserRepository>
                            implements UserService {

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
    }

}
