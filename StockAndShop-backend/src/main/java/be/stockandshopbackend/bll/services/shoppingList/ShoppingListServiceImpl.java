package be.stockandshopbackend.bll.services.shoppingList;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.bll.services.product.ProductService;
import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dal.repositories.ProductListItemRepository;
import be.stockandshopbackend.dal.repositories.ShoppingListRepository;
import be.stockandshopbackend.dl.entities.*;
import be.stockandshopbackend.exceptions.NotFoundException;
import be.stockandshopbackend.pl.DTOs.Response.ProductItemResponse;
import be.stockandshopbackend.pl.DTOs.websocket.ShoppingListEventDTO;
import be.stockandshopbackend.pl.DTOs.websocket.ShoppingListEventType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static be.stockandshopbackend.pl.DTOs.websocket.ShoppingListEventType.*;

@Service
public class ShoppingListServiceImpl extends BaseCRUDService<ShoppingList, Long, ShoppingListRepository>
                                    implements ShoppingListService {

    private final ProductService productService;
    private final ProductListItemRepository productListItemRepository;
    private final HomeRepository homeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ShoppingListServiceImpl(
            ShoppingListRepository repository,
            ProductService productService,
            ProductListItemRepository productListItemRepository,
            HomeRepository homeRepository,
            SimpMessagingTemplate messagingTemplate
    ){
        super(repository);
        this.productService = productService;
        this.productListItemRepository = productListItemRepository;
        this.homeRepository = homeRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingList findById(Long id) {
        return super.findById(id);
    }


    public List<ShoppingList> findByUser(User userDetails){
        List<Home> homes = homeRepository.findByUsers_User(userDetails);
        List<ShoppingList> sl = homes.stream()
                .map(Home::getShoppingLists)
                .flatMap(List::stream)
                .toList();
        System.out.println(sl);
        return sl;
    }

    //region WEBSOCKET

    // SecurityContextHolder used here because @AuthenticationPrincipal is only available at controller level.
    private String currentUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Pushes an event to every client subscribed to {@code /topic/shopping-list/{id}}.
     *
     * @param payload event-specific data; may be {@code null} (e.g. for {@code ITEM_TRANSFERRED})
     */
    private void broadcast(Long shoppingListId, ShoppingListEventType type, Object payload){
        messagingTemplate.convertAndSend(
                "/topic/shopping-list/" + shoppingListId,
                new ShoppingListEventDTO(type, shoppingListId, currentUsername(), payload)
        );
    }

    //endregion

    //region DELETE

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("No shopping list found with id : " + id);
        }
        // Removal from the parent collection triggers deletion via orphanRemoval declared on Home.shoppingLists
        homeRepository.findByShoppingListsId(id).ifPresent(home -> {
            home.getShoppingLists().removeIf(sl -> sl.getId().equals(id));
            homeRepository.save(home);
        });
    }

    @Transactional
    public void removeProductFromList(Long shoppingListId, int itemId){
        if (!repository.existsById(shoppingListId)) {
            throw new NotFoundException("ShoppingList with id " + shoppingListId + " not found");
        }
        productListItemRepository.deleteByIdAndShoppingListId(itemId, shoppingListId);
        broadcast(shoppingListId, ITEM_REMOVED, Map.of("itemId", itemId));
    }

    @Transactional
    public ShoppingList deleteProductCheckedAndAddStock(Long shoppingListId, UUID homeId) {
        ShoppingList shoppingList = repository.findById(shoppingListId).orElseThrow(
                () -> new NotFoundException("ShoppingList with id " + shoppingListId + " not found")
        );
        Home home = homeRepository.findById(homeId).orElseThrow(
                () -> new NotFoundException("Home not found with the id : " + homeId)
        );
        List<ProductListItem> checkedItems = shoppingList.getProducts().stream()
                .filter(ProductListItem::isChecked)
                .toList();
        for (ProductListItem item : checkedItems) {
            home.addProductStock(new ProductStockHome(item.getProduct(), item.getQuantity()));
            shoppingList.getProducts().remove(item);
        }
        homeRepository.save(home);
        repository.save(shoppingList);
        broadcast(shoppingListId, ITEM_TRANSFERRED, null);
        return shoppingList;
    }

    //endregion

    //region ADD

    @Transactional
    public Home createShoppingList(UUID homeId, String name, String description){
        Home home = homeRepository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        ShoppingList shoppingList = new ShoppingList(name, description);
        home.addShoppingList(shoppingList);
        return homeRepository.save(home);
    }

    @Transactional
    public void addProductToList(Long shoppingListId, String productName, int quantity){
        ShoppingList shoppingList = repository.findById(shoppingListId)
                .orElseThrow(() -> new NotFoundException("ShoppingList with id " + shoppingListId + " not found"));
        // Keep a reference before adding so we can broadcast the saved item without relying on getLast()
        ProductListItem newItem = new ProductListItem(productService.findOneByName(productName), quantity);
        shoppingList.addProduct(newItem);
        repository.save(shoppingList);
        broadcast(shoppingListId, ITEM_ADDED, ProductItemResponse.fromProductListItem(newItem));
    }

    @Transactional
    public void addListProductsToList(Long shoppingListId, List<ProductListItem> products){
        ShoppingList shoppingList = repository.findById(shoppingListId).orElseThrow(
                () -> new NotFoundException("ShoppingList with id " + shoppingListId + " not found")
        );
        for(ProductListItem productListItem : products){
            shoppingList.addProduct(productListItem);
        }
        repository.save(shoppingList);
        List<ProductItemResponse> added = products.stream()
                .map(ProductItemResponse::fromProductListItem)
                .toList();
        broadcast(shoppingListId, ITEMS_BATCH_ADDED, added);
    }

    //endregion
}
