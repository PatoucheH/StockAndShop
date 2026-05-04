package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.HomeService;
import be.stockandshopbackend.bll.services.ProductService;
import be.stockandshopbackend.bll.services.UserService;
import be.stockandshopbackend.dl.entities.Home;
import be.stockandshopbackend.dl.entities.ProductStockHome;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.entities.UserHome;
import be.stockandshopbackend.dl.enums.HomeRole;
import be.stockandshopbackend.pl.DTOs.Response.*;
import be.stockandshopbackend.pl.DTOs.requests.HomeRequest;
import be.stockandshopbackend.pl.DTOs.requests.ProductItemRequest;
import be.stockandshopbackend.pl.DTOs.requests.ShoppingListRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;
    private final ProductService productService;
    private final UserService userService;

    //region GET

    @GetMapping("admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<HomeResponse>> getHome() {
        return ResponseEntity.ok(homeService.findAll().stream().map(HomeResponse::fromHome).toList());
    }

    @GetMapping
    public ResponseEntity<List<HomeResponse>> getHome(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(homeService.findAllByUser((User) userDetails).stream()
                .map(HomeResponse::fromHome)
                .toList());
    }

    @GetMapping("/{id}/shopping-list")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<List<ShoppingListResponse>> getShoppingList(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllShoppingListsByHomeId(id).stream()
                .map(ShoppingListResponse::fromShoppingList)
                .toList());
    }

    @GetMapping("/{id}/stock")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<List<ProductItemResponse>> getStock(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllProductStockHomeByHomeId(id).stream()
                .map(ProductItemResponse::fromProductStockHome)
                .toList());
    }

    @GetMapping("/{id}/user")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<List<UserHomeResponse>> getUser(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllUserHomeByHomeId(id).stream()
                .map(UserHomeResponse::fromUserHome)
                .toList());
    }

    //endregion

    //region POST

    @PostMapping("/{id}/add-stock-product")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<ProductItemResponse> addProductStock(
            @PathVariable UUID id,
            @RequestBody @Valid ProductItemRequest productItemRequest
    ){
        ProductStockHome productStockHome = new ProductStockHome(
                productService.findOneByName(productItemRequest.name()),
                productItemRequest.quantity()
        );
        homeService.addProductStock(id, productStockHome);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ProductItemResponse.fromProductStockHome(productStockHome)
        );
    }

    @PostMapping("/{id}/add-user")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<UserHomeResponse> addUser(@PathVariable UUID id,
                                     @RequestParam UUID userId){
        UserHome userHome = new UserHome(userService.findById(userId), HomeRole.USER);
        homeService.addUserHome(id, userHome);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                UserHomeResponse.fromUserHome(userHome)
        );
    }

    @PostMapping
    public ResponseEntity<HomeResponse> createHome(@RequestBody @Valid HomeRequest h,
                                                   @AuthenticationPrincipal UserDetails user){
        Home home = new Home(h.name(), h.description());
        UserHome userHome = new UserHome((User) user, HomeRole.OWNER);
        home.addUserHome(userHome);
        return ResponseEntity.status(HttpStatus.CREATED).body(HomeResponse.fromHome(homeService.save(home)));
    }

    @PostMapping("/{id}/shopping-list")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<HomeResponse> createShoppingList(@PathVariable UUID id,
                                                           @RequestBody @Valid ShoppingListRequest shoppingList){
        return ResponseEntity.status(HttpStatus.CREATED).body(HomeResponse.fromHome(
                    homeService.createShoppingList(
                            id,
                            shoppingList.name(),
                            shoppingList.description()
                    )
                )
        );
    }

    //endregion

    //region DELETE

    @DeleteMapping("/{id}")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteHome(@PathVariable UUID id){
        homeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/delete-user")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUserHome(@PathVariable UUID id, @RequestParam UUID userId){
        homeService.deleteUserHome(id, userId);
        return ResponseEntity.noContent().build();
    }

    //endregion

    //region PUT

    @PutMapping("/{id}")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<HomeResponse> updateHome(@PathVariable UUID id,
                                                   @RequestBody @Valid HomeRequest h
    ){
        Home home = homeService.findById(id);
        home.setName(h.name());
        home.setDescription(h.description());
        homeService.save(home);
        return ResponseEntity.ok(HomeResponse.fromHome(home));
    }

    @PutMapping("/{id}/decrease-stock")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal) or hasAuthority('ADMIN')")
    public ResponseEntity<?> decreaseStock(@PathVariable UUID id,
                                           @RequestBody @Valid ProductItemRequest productItemRequest
    ){
        ProductStockHome productStockHome = new ProductStockHome(
                productService.findOneByName(productItemRequest.name()),
                productItemRequest.quantity()
        );
        homeService.decreaseStockProduct(id, productStockHome);
        return ResponseEntity.noContent().build();
    }

    //endregion
}
