package be.stockandshopbackend.bll.services.user.userService;

import be.stockandshopbackend.dl.entities.user.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> findAll();
    User findById(UUID id);
    User findByEmail(String email);
    List<User> searchByQuery(String query);

    /** Updates the connected user's display name (username). */
    void updateUsername(User user, String username);

    /** Changes the connected user's password after verifying the current one. */
    void changePassword(User user, String currentPassword, String newPassword);

    /**
     * Anonymizes the given user's account (erases identifying data, keeps memberships/history intact).
     * Blocked (ConflictException) if the user still owns a home or has a non-zero balance in any home.
     */
    void deleteAccount(User user);
}
