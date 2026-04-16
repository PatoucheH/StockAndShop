package be.stockandshop.controllers;

import be.stockandshop.entities.ShoppingList;
import be.stockandshop.services.ShoppingListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shoppingList")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @GetMapping("/{id}")
    public ShoppingList getShoppingListById(@PathVariable Long id) {
        return shoppingListService.getShoppingListById(id);
    }

    @PostMapping
    public ShoppingList createShoppingList(@RequestBody ShoppingList shoppingList) {
        return shoppingListService.saveShoppingList(shoppingList);
    }

    @DeleteMapping
    public void deleteShoppingList(@RequestBody Long id) {
        shoppingListService.deleteShoppingListById(id);
    }
}
