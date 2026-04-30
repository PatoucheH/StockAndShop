package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.HomeService;
import be.stockandshopbackend.bll.services.ProductService;
import be.stockandshopbackend.dl.entities.Home;
import be.stockandshopbackend.dl.entities.ProductStockHome;
import be.stockandshopbackend.pl.DTOs.Response.HomeResponse;
import be.stockandshopbackend.pl.DTOs.Response.ProductItemResponse;
import be.stockandshopbackend.pl.DTOs.Response.ShoppingListResponse;
import be.stockandshopbackend.pl.DTOs.requests.HomeRequest;
import be.stockandshopbackend.pl.DTOs.requests.ProductItemRequest;
import be.stockandshopbackend.pl.DTOs.requests.ShoppingListRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;
    private final ProductService productService;

    //region GET

    @GetMapping
    public ResponseEntity<List<HomeResponse>> getHome(){
        return ResponseEntity.ok(homeService.findAll().stream().map(HomeResponse::fromHome).toList());
    }

    @GetMapping("/{id}/shopping-list")
    public ResponseEntity<List<ShoppingListResponse>> getShoppingList(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllShoppingListsByHomeId(id).stream()
                .map(ShoppingListResponse::fromShoppingList)
                .toList());
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<List<ProductItemResponse>> getStock(@PathVariable UUID id){
        return ResponseEntity.ok(homeService.findAllProductStockHomeByHomeId(id).stream()
                .map(ProductItemResponse::fromProductStockHome)
                .toList());
    }

    //endregion

    //region POST

    @PostMapping("/{id}/add-stock-product")
    public ResponseEntity<?> addProductStock(
            @PathVariable UUID id,
            @RequestBody @Valid ProductItemRequest productItemRequest
    ){
        ProductStockHome productStockHome = new ProductStockHome(
                productService.findOneByName(productItemRequest.name()),
                productItemRequest.quantity()
        );
        homeService.addProductStock(id, productStockHome);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<HomeResponse> createHome(@RequestBody @Valid HomeRequest h){
        Home home = new Home(h.name(), h.description());
        return ResponseEntity.ok(HomeResponse.fromHome(homeService.save(home)));
    }

    @PostMapping("/{id}/shopping-list")
    public ResponseEntity<HomeResponse> createShoppingList(@PathVariable UUID id,
                                                           @RequestBody @Valid ShoppingListRequest shoppingList){
        return ResponseEntity.ok(HomeResponse.fromHome(
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
    public ResponseEntity<?> deleteHome(@PathVariable UUID id){
        homeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //endregion

    //region PUT

    @PutMapping("/{id}")
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
    public ResponseEntity<?> decreaseStock(@PathVariable UUID id,
                                           @RequestBody @Valid ProductItemRequest productItemRequest
    ){
        ProductStockHome productStockHome = new ProductStockHome(
                productService.findOneByName(productItemRequest.name()),
                productItemRequest.quantity()
        );
        homeService.decreaseStockProduct(id, productStockHome);
        return ResponseEntity.ok().build();
    }

    //endregion
}
