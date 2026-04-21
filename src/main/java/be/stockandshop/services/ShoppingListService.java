package be.stockandshop.services;

import be.stockandshop.entities.Product;
import be.stockandshop.entities.ProductListLine;
import be.stockandshop.entities.ShoppingList;
import be.stockandshop.repositories.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingListService {
    private final ProductService productService;
    private final ProductListLineService productListlineService;
    private final ShoppingListRepository shoppingListRepository;

    public ShoppingList getShoppingListById(Long id) {
        return shoppingListRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Shopping list not found with id " + id)
        );
    }

    public ShoppingList saveShoppingList(ShoppingList shoppingList) {
        return shoppingListRepository.save(shoppingList);
    }

    public void deleteShoppingListById(Long id) {
        shoppingListRepository.deleteById(id);
    }

    public ShoppingList addProduct(Long productId, Long shoppingListId, Integer quantity) {
        ShoppingList shoppingList = getShoppingListById(shoppingListId);
        Product productToAdd = productService.findById(productId);
        ProductListLine product = new ProductListLine(productToAdd, quantity);
        productListlineService.save(product);
        shoppingList.getProducts().add(product);
        return shoppingListRepository.save(shoppingList);
    }

    public ShoppingList removeProduct(Long productListLineId, Long shoppingListId) {
        ShoppingList shoppingList = getShoppingListById(shoppingListId);
        ProductListLine product = productListlineService.findById(productListLineId);
        productListlineService.delete(product);
        shoppingList.getProducts().remove(product);
        return shoppingListRepository.save(shoppingList);

    }
}
