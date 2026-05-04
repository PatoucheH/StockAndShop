package be.stockandshopbackend.bll.services.security;

import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.enums.HomeRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

//     @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal)") CHECK IF OWNER OF HOME or ADMIN
//     @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)") CHECK IF USER IS IN HOME or ADMIN

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

    public boolean isInHomeByProductItem(Long productListItemId, User user){
        if(isAdmin(user)) return true;
        return homeRepository.findByShoppingLists_ProductsId(productListItemId)
                .map(home -> home.getUsers().stream()
                        .anyMatch(uh -> uh.getUser().equals(user)))
                .orElse(false);
    }

}
