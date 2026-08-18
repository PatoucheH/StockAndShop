package be.stockandshopbackend.bll.services.user.userService;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.home.HomeRepository;
import be.stockandshopbackend.dal.repositories.user.RefreshTokenRepository;
import be.stockandshopbackend.dal.repositories.user.UserFavoriteRecipeRepository;
import be.stockandshopbackend.dal.repositories.user.UserFavoriteShoppingListRepository;
import be.stockandshopbackend.dal.repositories.user.UserRepository;
import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.home.UserHome;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.dl.enums.HomeRole;
import be.stockandshopbackend.exceptions.ConflictException;
import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl extends BaseCRUDService<User, UUID, UserRepository>
                            implements UserService {

    private final HomeRepository homeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserFavoriteRecipeRepository userFavoriteRecipeRepository;
    private final UserFavoriteShoppingListRepository userFavoriteShoppingListRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           HomeRepository homeRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           UserFavoriteRecipeRepository userFavoriteRecipeRepository,
                           UserFavoriteShoppingListRepository userFavoriteShoppingListRepository,
                           PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.homeRepository = homeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userFavoriteRecipeRepository = userFavoriteRecipeRepository;
        this.userFavoriteShoppingListRepository = userFavoriteShoppingListRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No user found with email: " + email));
    }

    @Override
    public List<User> searchByQuery(String query) {
        return repository.findByUsernameContainingIgnoreCaseOrEmailIgnoreCase(query, query);
    }

    @Override
    @Transactional
    public void updateUsername(User principal, String username) {
        User user = repository.findById(principal.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setUsername(username.trim());
        repository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(User principal, String currentPassword, String newPassword) {
        User user = repository.findById(principal.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe actuel incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    @Override
    @Transactional
    public void deleteAccount(User principal) {
        User user = repository.findById(principal.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 1. Pre-checks: refuse while owner or not settled — this is what keeps the OTHER members'
        //    expenses, history and balances completely untouched by a deletion.
        for (Home home : homeRepository.findByUsers_User(user)) {
            UserHome membership = membershipOf(home, user);
            if (membership.getHomeRole() == HomeRole.OWNER) {
                throw new ConflictException(
                        "Vous êtes propriétaire de la maison « " + home.getName()
                        + " ». Transférez la propriété ou supprimez cette maison avant de supprimer votre compte.");
            }
            if (membership.getBalance() != 0) {
                throw new ConflictException(
                        "Votre solde n'est pas réglé dans la maison « " + home.getName()
                        + " ». Réglez vos comptes avant de supprimer votre compte.");
            }
        }

        // 2. Erase personal data: kill sessions and private preferences
        refreshTokenRepository.deleteByUser(user);
        userFavoriteRecipeRepository.deleteByUser(user);
        userFavoriteShoppingListRepository.deleteByUser(user);

        // 3. Anonymize in place. The UserHome memberships and every expense/refund stay intact
        //    (shown as "Utilisateur supprimé"), so nothing changes for the remaining members.
        //    The account carries no identifying data anymore and can no longer log in.
        user.setUsername("Utilisateur supprimé");
        user.setEmail("deleted-" + user.getId() + "@deleted.invalid");
        user.setPassword("DELETED-" + UUID.randomUUID());
        user.getRoles().clear();
        repository.save(user);
    }

    private UserHome membershipOf(Home home, User user) {
        return home.getUsers().stream()
                .filter(uh -> uh.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Membership not found in home: " + home.getId()));
    }
}
