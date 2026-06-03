package be.stockandshopbackend.bll.services.shoppingList;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.bll.services.product.ProductService;
import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dal.repositories.ProductListItemRepository;
import be.stockandshopbackend.dal.repositories.ShoppingListRepository;
import be.stockandshopbackend.dl.entities.*;
import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ShoppingListServiceImpl extends BaseCRUDService<ShoppingList, Long, ShoppingListRepository>
                                    implements ShoppingListService {

    private final ProductService productService;
    private final ProductListItemRepository productListItemRepository;
    private final HomeRepository homeRepository;

    public ShoppingListServiceImpl(ShoppingListRepository repository, ProductService productService, ProductListItemRepository productListItemRepository, HomeRepository homeRepository){
        super(repository);
        this.productService = productService;
        this.productListItemRepository = productListItemRepository;
        this.homeRepository = homeRepository;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("No shopping list found with id : " + id);
        }
        homeRepository.findByShoppingListsId(id).ifPresent(home -> {
            home.getShoppingLists().removeIf(sl -> sl.getId().equals(id));
            homeRepository.save(home);
        });
    }

    public void removeProductFromList(Long shoppingListId, String productName){
        if (!repository.existsById(shoppingListId)) {
            throw new NotFoundException("ShoppingList with id " + shoppingListId + " not found");
        }
        productListItemRepository.deleteByShoppingListIdAndProductName(shoppingListId, productName);
    }

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
    public void addProductFromList(Long shoppingListId, String productName, int quantity){
        ShoppingList shoppingList = repository.findById(shoppingListId)
                .orElseThrow(() -> new NotFoundException("ShoppingList with id " + shoppingListId + " not found"));
        shoppingList.addProduct(new ProductListItem(productService.findOneByName(productName), quantity));
        repository.save(shoppingList);
    }

    //endregion

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
        return shoppingList;
    }

}
