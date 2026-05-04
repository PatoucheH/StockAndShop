package be.stockandshopbackend.bll.services;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.UserRepository;
import be.stockandshopbackend.dl.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService extends BaseCRUDService<User, UUID, UserRepository> {

    public UserService(UserRepository userRepository) {
        super(userRepository);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
