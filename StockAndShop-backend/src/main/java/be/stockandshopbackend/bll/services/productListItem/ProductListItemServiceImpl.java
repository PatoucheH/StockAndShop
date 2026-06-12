package be.stockandshopbackend.bll.services.productListItem;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.ProductListItemRepository;
import be.stockandshopbackend.dal.repositories.ShoppingListRepository;
import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ShoppingList;
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
