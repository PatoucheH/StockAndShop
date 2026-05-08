package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.shoppingList.ShoppingListService;
import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.pl.DTOs.Response.HomeResponse;
import be.stockandshopbackend.pl.DTOs.Response.ProductItemResponse;
import be.stockandshopbackend.pl.DTOs.Response.ShoppingListResponse;
import be.stockandshopbackend.pl.DTOs.requests.ProductItemRequest;
import be.stockandshopbackend.pl.DTOs.requests.ShoppingListRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    //region GET
    @GetMapping("/{id}")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<ShoppingListResponse> getShoppingListById(@PathVariable Long id){
       return ResponseEntity.ok(ShoppingListResponse.fromShoppingList(shoppingListService.findById(id)));
    }


    @GetMapping("/{id}/product")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<ProductItemResponse>> getAllItemOfAList(@PathVariable Long id){
        ShoppingList shoppingList = shoppingListService.findById(id);
        return ResponseEntity.ok(shoppingList.getProducts().stream()
                .map(ProductItemResponse::fromProductListItem)
                .toList());
    }

    //endregion

    //region POST

    @PostMapping("/{id}")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<HomeResponse> createShoppingList(@PathVariable UUID id,
                                                           @RequestBody @Valid ShoppingListRequest shoppingList){
        return ResponseEntity.status(HttpStatus.CREATED).body(HomeResponse.fromHome(
                        shoppingListService.createShoppingList(
                                id,
                                shoppingList.name(),
                                shoppingList.description()
                        )
                )
        );
    }

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
