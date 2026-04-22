package be.stockandshop.pl.controllers;

import be.stockandshop.bll.services.interfaces.shoppingList.ShoppingListCommandService;
import be.stockandshop.bll.services.interfaces.shoppingList.ShoppingListQueryService;
import be.stockandshop.pl.dto.reponses.ShoppingListResponse;
import be.stockandshop.pl.dto.requests.ShoppingListRequest;
import be.stockandshop.bll.services.implementations.shoppingList.ShoppingListCommandImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shoppingList")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListCommandService shoppingListCommand;
    private final ShoppingListQueryService shoppingListQuery;

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListResponse> getShoppingListById(@PathVariable Long id) {
        return ResponseEntity.ok(shoppingListQuery.getShoppingListById(id));
    }

    @PostMapping
    public ResponseEntity<ShoppingListResponse> createShoppingList(@RequestBody ShoppingListRequest shoppingList) {
        return ResponseEntity.ok(shoppingListCommand.save(shoppingList));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteShoppingList(@RequestBody Long id) {
        shoppingListCommand.deleteShoppingListById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<ShoppingListResponse> addShoppingList(@RequestBody ShoppingListRequest shoppingList) {
        return ResponseEntity.ok(shoppingListCommand.save(shoppingList));
    }
}
