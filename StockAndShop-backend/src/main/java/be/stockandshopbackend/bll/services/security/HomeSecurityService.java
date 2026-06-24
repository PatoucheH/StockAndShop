package be.stockandshopbackend.bll.services.security;

import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.enums.HomeRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * Spring Security bean used as a SpEL expression target in {@code @PreAuthorize} annotations.
 * Register as "homeSecurity" so controllers can write:
 * <pre>
 *   @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal)")
 *   @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
 * </pre>
 * Overloaded by ID type (UUID for home, Long for shopping list / product-list item).
 * Admins bypass all home-level checks.
 */
@Service("homeSecurity")
@RequiredArgsConstructor
public class HomeSecurityService {

    private final HomeRepository homeRepository;

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ADMIN"));
    }

    public boolean isOwner(UUID homeId, User user){
        if(isAdmin(user)) return true;
        return homeRepository.findById(homeId)
                .map(home -> home.getUsers().stream()
                        .anyMatch(uh -> uh.getUser().equals(user)
                                && uh.getHomeRole() == HomeRole.OWNER))
                .orElse(false);
    }

    public boolean isInHome(UUID homeId, User user){
        if(isAdmin(user)) return true;
        return homeRepository.findById(homeId)
                .map(home -> home.getUsers().stream()
                        .anyMatch(uh -> uh.getUser().equals(user)))
                .orElse(false);
    }

    public boolean isOwner(Long shoppingListId, User user){
        if(isAdmin(user)) return true;
        return homeRepository.findByShoppingListsId(shoppingListId)
                .map(home -> home.getUsers().stream()
                        .anyMatch(uh -> uh.getUser().equals(user)
                                && uh.getHomeRole() == HomeRole.OWNER))
                .orElse(false);
    }

    public boolean isInHome(Long shoppingListId, User user){
        if(isAdmin(user)) return true;
        return homeRepository.findByShoppingListsId(shoppingListId)
                .map(home -> home.getUsers().stream()
                        .anyMatch(uh -> uh.getUser().equals(user)))
                .orElse(false);
    }

    /** Navigates Home → ShoppingList → ProductListItem to check membership — used by ProductListItemController. */
    public boolean isInHomeByProductItem(Long productListItemId, User user){
        if(isAdmin(user)) return true;
        return homeRepository.findByShoppingLists_ProductsId(productListItemId)
                .map(home -> home.getUsers().stream()
                        .anyMatch(uh -> uh.getUser().equals(user)))
                .orElse(false);
    }

}
