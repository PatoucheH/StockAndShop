package be.stockandshopbackend.pl.controllers.home;

import be.stockandshopbackend.bll.services.home.HomeService;
import be.stockandshopbackend.bll.services.productAndShoppingList.product.ProductService;
import be.stockandshopbackend.bll.services.user.userService.UserService;
import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.product.ProductStockHome;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.dl.entities.home.UserHome;
import be.stockandshopbackend.dl.enums.HomeRole;
import be.stockandshopbackend.pl.DTOs.Response.*;
import be.stockandshopbackend.pl.DTOs.Response.home.HomeResponse;
import be.stockandshopbackend.pl.DTOs.Response.home.UserHomeResponse;
import be.stockandshopbackend.pl.DTOs.Response.products.ProductItemResponse;
import be.stockandshopbackend.pl.DTOs.requests.home.AddUserToHomeRequest;
import be.stockandshopbackend.pl.DTOs.requests.home.HomeRequest;
import be.stockandshopbackend.pl.DTOs.requests.products.ProductItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<HomeResponse> getHomeById(@PathVariable UUID id){
        return ResponseEntity.ok(HomeResponse.fromHome(homeService.findById(id)));
    }

    @GetMapping("/{id}/shopping-list")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<ShoppingListResponse>> getShoppingList(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllShoppingListsByHomeId(id).stream()
                .map(ShoppingListResponse::fromShoppingList)
                .toList());
    }

    @GetMapping("/{id}/stock")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<ProductItemResponse>> getStock(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllProductStockHomeByHomeId(id).stream()
                .map(ProductItemResponse::fromProductStockHome)
                .toList());
    }

    @GetMapping("/{id}/user")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<UserHomeResponse>> getUser(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllUserHomeByHomeId(id).stream()
                .map(UserHomeResponse::fromUserHome)
                .toList());
    }

    //endregion

    //region POST

    @PostMapping("/{id}/add-stock-product")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
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
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal)")
    public ResponseEntity<UserHomeResponse> addUser(
            @PathVariable UUID id,
            @RequestBody @Valid AddUserToHomeRequest req
    ){
        // OWNER cannot be assigned via invitation; only the home creator gets OWNER (see POST /home)
        HomeRole role = (req.role() == null || req.role() == HomeRole.OWNER) ? HomeRole.USER : req.role();
        UserHome userHome = new UserHome(userService.findByEmail(req.email()), role, 0);
        homeService.addUserHome(id, userHome);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                UserHomeResponse.fromUserHome(userHome)
        );
    }

    // Creator is automatically assigned OWNER; no subsequent role change can reassign OWNER (see addUser)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HomeResponse> createHome(@RequestBody @Valid HomeRequest h,
                                                   @AuthenticationPrincipal UserDetails user){
        Home home = new Home(h.name(), h.description());
        UserHome userHome = new UserHome((User) user, HomeRole.OWNER, 0);
        home.addUserHome(userHome);
        return ResponseEntity.status(HttpStatus.CREATED).body(HomeResponse.fromHome(homeService.save(home)));
    }

    //endregion

    //region DELETE

    @DeleteMapping("/{id}")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal)")
    public ResponseEntity<?> deleteHome(@PathVariable UUID id){
        homeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/delete-user")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal)")
    public ResponseEntity<?> deleteUserHome(@PathVariable UUID id, @RequestParam UUID userId){
        homeService.deleteUserHome(id, userId);
        return ResponseEntity.noContent().build();
    }

    //endregion

    //region PUT

    @PutMapping("/{id}")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal)")
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
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
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
