package be.stockandshop.bll.services.implementations.shoppingList;

import be.stockandshop.bll.services.interfaces.shoppingList.ShoppingListQueryService;
import be.stockandshop.dal.repositories.ShoppingListRepository;
import be.stockandshop.pl.dto.reponses.ShoppingListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingListQueryImpl implements ShoppingListQueryService {

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListResponse getShoppingListById(Long id) {
        return shoppingListRepository.findById(id)
                .map(ShoppingListResponse::new)
                .orElseThrow(
                        () -> new RuntimeException("Shopping list not found with id " + id)
                );
    }
}
