package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.product.ProductService;
import be.stockandshopbackend.bll.services.shoppingList.ShoppingListService;
import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.dl.entities.User;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final ProductService productService;

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

    @GetMapping("/all")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<List<ShoppingListResponse>> getShoppingListsByUser(@AuthenticationPrincipal UserDetails userDetails){
        List<ShoppingList> shoppingList = shoppingListService.findByUser((User) userDetails);
        return ResponseEntity.ok(shoppingList.stream()
                .map(ShoppingListResponse::fromShoppingList)
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

    @PostMapping("/{id}/add-product")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<ShoppingListResponse> addItemToShoppingList(@PathVariable Long id,
                                                                      @RequestBody @Valid ProductItemRequest request){
        shoppingListService.addProductToList(id, request.name(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ShoppingListResponse.fromShoppingList(shoppingListService.findById(id))
        );
    }

    @PostMapping("/{id}/add-list-products")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<ShoppingListResponse> addListProductsToShoppingList(
            @PathVariable Long id,
            @RequestBody @Valid List<ProductItemRequest> request)
    {
        List<ProductListItem> products = request.stream()
                        .map(r -> new ProductListItem(
                            productService.findOneByName(r.name()),
                            r.quantity()
                        ))
                        .toList();
        shoppingListService.addListProductsToList(id, products);
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

    @DeleteMapping("/{id}/remove-product/{productId}")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<?> removeItemFromShoppingList(@PathVariable Long id,
                                                          @PathVariable int productId){
        shoppingListService.removeProductFromList(id, productId);
        return ResponseEntity.noContent().build();
    }

    //endregion

    //region OTHER

    @PatchMapping("/{id}/home/{homeId}/checked-list")
    @PreAuthorize("@homeSecurity.isInHome(#id, authentication.principal)")
    public ResponseEntity<ShoppingListResponse> checkedProductAndAddStock(@PathVariable Long id,
                                                                          @PathVariable UUID homeId){
        return ResponseEntity.ok(
                ShoppingListResponse.fromShoppingList(
                        shoppingListService.deleteProductCheckedAndAddStock(id, homeId)
                )
        );
    }

    //endregion
}
