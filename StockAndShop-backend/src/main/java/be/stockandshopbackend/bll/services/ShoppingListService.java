package be.stockandshopbackend.bll.services;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dal.repositories.ProductListItemRepository;
import be.stockandshopbackend.dal.repositories.ShoppingListRepository;
import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShoppingListService extends BaseCRUDService<ShoppingList, Long, ShoppingListRepository> {

    private final ProductService productService;
    private final ProductListItemRepository productListItemRepository;
    private final HomeRepository homeRepository;

    public ShoppingListService(ShoppingListRepository repository, ProductService productService, ProductListItemRepository productListItemRepository, HomeRepository homeRepository){
        super(repository);
        this.productService = productService;
        this.productListItemRepository = productListItemRepository;
        this.homeRepository = homeRepository;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("No record found with id : " + id);
        }
        homeRepository.findByShoppingListsId(id).ifPresent(home -> {
            home.getShoppingLists().removeIf(sl -> sl.getId().equals(id));
            homeRepository.save(home);
        });
    }

    //region ADD / REMOVE
    public void addProductFromList(Long shoppingListId, String productName, int quantity){
        ShoppingList shoppingList = repository.findById(shoppingListId)
                .orElseThrow(() -> new NotFoundException("ShoppingList with id " + shoppingListId + " not found"));
        shoppingList.addProduct(new ProductListItem(productService.findOneByName(productName), quantity));
        repository.save(shoppingList);
    }

    public void removeProductFromList(Long shoppingListId, String productName){
        if (!repository.existsById(shoppingListId)) {
            throw new NotFoundException("ShoppingList with id " + shoppingListId + " not found");
        }
        productListItemRepository.deleteByShoppingListIdAndProductName(shoppingListId, productName);
    }

    //endregion

}
