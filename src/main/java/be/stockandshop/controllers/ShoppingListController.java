package be.stockandshop.controllers;

import be.stockandshop.dto.reponses.ShoppingListResponse;
import be.stockandshop.dto.requests.ShoppingListRequest;
import be.stockandshop.entities.ShoppingList;
import be.stockandshop.services.ShoppingListService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shoppingList")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListResponse> getShoppingListById(@PathVariable Long id) {
        return ResponseEntity.ok(shoppingListService.getShoppingListById(id));
    }

    @PostMapping
    public ResponseEntity<ShoppingListResponse> createShoppingList(@RequestBody ShoppingListRequest shoppingList) {
        return ResponseEntity.ok(shoppingListService.save(shoppingList));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteShoppingList(@RequestBody Long id) {
        shoppingListService.deleteShoppingListById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<ShoppingListResponse> addShoppingList(@RequestBody ShoppingListRequest shoppingList) {
        return ResponseEntity.ok(shoppingListService.save(shoppingList));
    }
}
