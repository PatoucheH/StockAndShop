package be.stockandshopbackend.pl.controllers.user;

import be.stockandshopbackend.bll.services.user.userFavorite.UserFavoriteShoppingListService;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.pl.DTOs.Response.ShoppingListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shopping-list")
public class UserFavoriteShoppingListController {

    private final UserFavoriteShoppingListService favoriteService;

    @GetMapping("/favorites")
    public ResponseEntity<List<ShoppingListResponse>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                favoriteService.getFavorites((User) userDetails).stream()
                        .map(ShoppingListResponse::fromShoppingList)
                        .toList()
        );
    }

    @PostMapping("/{id}/favorite")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<?> addFavorite(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.addFavorite(id, (User) userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<?> removeFavorite(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        favoriteService.removeFavorite(id, (User) userDetails);
        return ResponseEntity.noContent().build();
    }
}
