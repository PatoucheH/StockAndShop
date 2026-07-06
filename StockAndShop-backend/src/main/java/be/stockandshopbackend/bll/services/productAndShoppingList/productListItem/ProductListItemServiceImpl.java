package be.stockandshopbackend.bll.services.productAndShoppingList.productListItem;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.product.ProductListItemRepository;
import be.stockandshopbackend.dal.repositories.ShoppingListRepository;
import be.stockandshopbackend.dl.entities.product.ProductListItem;
import be.stockandshopbackend.exceptions.NotFoundException;
import be.stockandshopbackend.pl.DTOs.websocket.ShoppingListEventDTO;
import be.stockandshopbackend.pl.DTOs.websocket.ShoppingListEventType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProductListItemServiceImpl extends BaseCRUDService<ProductListItem, Long, ProductListItemRepository>
                                        implements ProductListItemService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ShoppingListRepository shoppingListRepository;

    public ProductListItemServiceImpl(ProductListItemRepository repository,
                                      SimpMessagingTemplate messagingTemplate,
                                      ShoppingListRepository shoppingListRepository) {
        super(repository);
        this.messagingTemplate = messagingTemplate;
        this.shoppingListRepository = shoppingListRepository;
    }

    /**
     * Toggles the checked state of an item and broadcasts the change to all list subscribers.
     */
    @Transactional
    public void checkedItem(Long productListItemId) {
        ProductListItem productListItem = repository.findById(productListItemId)
                .orElseThrow(() -> new NotFoundException("ProductListItem not found with id: " + productListItemId));
        productListItem.toggleIsChecked();
        repository.save(productListItem);

        // ProductListItem has no back-reference to ShoppingList, so we look it up via the repository
        shoppingListRepository.findByProductsId(productListItemId).ifPresent(shoppingList ->
            messagingTemplate.convertAndSend(
                "/topic/shopping-list/" + shoppingList.getId(),
                new ShoppingListEventDTO(
                    ShoppingListEventType.ITEM_TOGGLED,
                    shoppingList.getId(),
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    Map.of("itemId", productListItemId, "isChecked", productListItem.isChecked())
                )
            )
        );
    }
}
