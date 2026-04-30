package be.stockandshopbackend.bll.services;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.HomeRepository;
import be.stockandshopbackend.dl.entities.Home;
import be.stockandshopbackend.dl.entities.Product;
import be.stockandshopbackend.dl.entities.ProductStockHome;
import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HomeService extends BaseCRUDService<Home, UUID, HomeRepository> {

    private final ProductService productService;

    public HomeService(HomeRepository homeRepository, ProductService productService) {
        super(homeRepository);
        this.productService = productService;
    }

    //region FIND

    public List<ShoppingList> findAllShoppingListsByHomeId(UUID homeId) {
        List<ShoppingList> sl = repository.findById(homeId).get().getShoppingLists();
        if (sl.isEmpty()) {
            throw new NotFoundException("No shopping list found with id: " + homeId);
        }
        return sl;
    }

    public List<ProductStockHome> findAllProductStockHomeByHomeId(UUID homeId) {
        List<ProductStockHome> psh = repository.findById(homeId).get().getStocks();
        if (psh.isEmpty()) {
            throw new NotFoundException("No product stock home found with id: " + homeId);
        }
        return psh;
    }

    //endregion


    //region CREATE / ADD

    public Home createShoppingList(UUID homeId, String name, String description){
        Home home = repository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        ShoppingList shoppingList = new ShoppingList(name, description);
        home.addShoppingList(shoppingList);
        return repository.save(home);
    }

    public void addProductStock(UUID homeId, ProductStockHome request){
        Home home = repository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        Product product = productService.findOneByName(request.getProduct().getName());
        ProductStockHome  productStockHome = new ProductStockHome(product, request.getQuantity());
        home.addProductStock(productStockHome);
        repository.save(home);
    }

    //endregion

    //region DELETE / REMOVE

    @Transactional
    public void decreaseStockProduct(UUID homeId, ProductStockHome request){
        Home home = repository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        Product product = productService.findOneByName(request.getProduct().getName());
        home.getStocks().stream()
                .filter(s -> s.getProduct().getName().equals(product.getName()))
                .findFirst()
                .ifPresent(stock -> {
                    stock.setQuantity(stock.getQuantity() -  request.getQuantity());
                    if(stock.getQuantity() <= 0){
                        home.getStocks().remove(stock);
                    }
                });
        repository.save(home);
    }

    //endregion
}
