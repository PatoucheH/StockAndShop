package be.stockandshopbackend.bll.services.userService;

import be.stockandshopbackend.dl.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    /// GET
    List<User> findAll();
    User findById(UUID id);
}
