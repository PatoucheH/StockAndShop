package be.stockandshopbackend.bll.services.userService;

import be.stockandshopbackend.dl.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> findAll();
    User findById(UUID id);
    User findByEmail(String email);
    List<User> searchByQuery(String query);
}
