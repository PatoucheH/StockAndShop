package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.HomeService;
import be.stockandshopbackend.bll.services.ShoppingListService;
import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.pl.DTOs.Response.ProductItemResponse;
import be.stockandshopbackend.pl.DTOs.Response.ShoppingListResponse;
import be.stockandshopbackend.pl.DTOs.requests.ProductItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final HomeService homeService;

    //region GET

    @GetMapping("/{id}")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<ProductItemResponse>> getAllItemOfAList(@PathVariable Long id){
        ShoppingList shoppingList = shoppingListService.findById(id);
        return ResponseEntity.ok(shoppingList.getProducts().stream()
                .map(ProductItemResponse::fromProductListItem)
                .toList());
    }

    //endregion

    //region POST

    @PostMapping("/{id}/add")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<ShoppingListResponse> addItemToShoppingList(@PathVariable Long id,
                                                                      @RequestBody @Valid ProductItemRequest request){
        shoppingListService.addProductFromList(id, request.name(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ShoppingListResponse.fromShoppingList(shoppingListService.findById(id))
        );
    }

    //endregion

    //region DELETE

    @DeleteMapping("/{id}")
    @PreAuthorize("@homeSecurity.isOwner(#id, authentication.principal)")
    public ResponseEntity<?> deleteShoppingList(@PathVariable Long id){
        shoppingListService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/remove")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<?> removeItemFromToShoppingList(@PathVariable Long id,
                                                          @RequestParam String name){
        shoppingListService.removeProductFromList(id, name);
        return ResponseEntity.noContent().build();
    }

    //endregion

}
