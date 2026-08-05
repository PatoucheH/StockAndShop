package be.stockandshopbackend.bll.services.home;

import be.stockandshopbackend.dl.entities.*;
import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.product.ProductStockHome;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.dl.entities.home.UserHome;

import java.util.List;
import java.util.UUID;

public interface HomeService {

    /// GET
    List<Home> findAll();
    Home findById(UUID id);
    List<Home> findAllByUser(User user);
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

    /// ROLES
    void transferOwnership(UUID homeId, UUID newOwnerUserId);

}
