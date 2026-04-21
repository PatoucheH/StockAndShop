package be.stockandshop.services;

import be.stockandshop.dto.reponses.ShoppingListResponse;
import be.stockandshop.dto.requests.ShoppingListRequest;
import be.stockandshop.entities.Product;
import be.stockandshop.entities.ProductListLine;
import be.stockandshop.entities.ShoppingList;
import be.stockandshop.repositories.ProductListLineRepository;
import be.stockandshop.repositories.ProductRepository;
import be.stockandshop.repositories.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingListService {
    private final ProductRepository productRepository;
    private final ProductListLineRepository productListLineRepository;
    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListResponse getShoppingListById(Long id) {
        return shoppingListRepository.findById(id)
                .map(ShoppingListResponse::new)
                .orElseThrow(
                () -> new RuntimeException("Shopping list not found with id " + id)
        );
    }

    public ShoppingListResponse save(ShoppingListRequest shoppingList) {
        if(shoppingList.getDescription() == null ){
            return new ShoppingListResponse(shoppingListRepository.save(
                    new ShoppingList(shoppingList.getName())));
        }
        return new ShoppingListResponse(shoppingListRepository.save(
                new ShoppingList(shoppingList.getName(), shoppingList.getDescription())));
    }

    public void deleteShoppingListById(Long id) {
        shoppingListRepository.deleteById(id);
    }

    @Transactional
    public ShoppingListResponse addProduct(Long productId, Long shoppingListId, Integer quantity) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        ProductListLine pll = new ProductListLine(product, quantity);
        shoppingList.addProduct(pll);
        productListLineRepository.save(pll);
        return new ShoppingListResponse(shoppingListRepository.save(shoppingList));
    }

    @Transactional
    public ShoppingListResponse removeProduct(Long productListLineId, Long shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId).orElseThrow();
        ProductListLine pll = productListLineRepository.findById(productListLineId).orElseThrow();
        shoppingList.removeProduct(pll);
        productListLineRepository.delete(pll);
        return new ShoppingListResponse(shoppingListRepository.save(shoppingList));
    }
}
