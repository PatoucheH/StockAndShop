package be.stockandshopbackend.bll.services.home;

import be.stockandshopbackend.dl.entities.*;

import java.util.List;
import java.util.UUID;

public interface HomeService {

    /// GET
    List<Home> findAll();
    Home findById(UUID id);
    List<Home> findAllByUser(User user);
    Home findHomeById(UUID id);
    List<ShoppingList> findAllShoppingListsByHomeId(UUID homeId);
    List<ProductStockHome> findAllProductStockHomeByHomeId(UUID homeId);
    List<UserHome> findAllUserHomeByHomeId(UUID homeId);

    /// ADD
    Home save(Home home);
    void addProductStock(UUID homeId, ProductStockHome request);
    void addUserHome(UUID homeId, UserHome request);

    /// DELETE
    void deleteById(UUID id);
    void decreaseStockProduct(UUID homeId, ProductStockHome request);
    void deleteUserHome(UUID homeId, UUID userId);

}
