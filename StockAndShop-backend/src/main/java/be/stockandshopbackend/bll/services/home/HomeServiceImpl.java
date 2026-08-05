package be.stockandshopbackend.bll.services.home;

import be.stockandshopbackend.bll.services.productAndShoppingList.product.ProductService;
import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.home.HomeRepository;
import be.stockandshopbackend.dl.entities.*;
import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.entities.product.ProductStockHome;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.dl.entities.home.UserHome;
import be.stockandshopbackend.dl.enums.HomeRole;
import be.stockandshopbackend.exceptions.ConflictException;
import be.stockandshopbackend.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HomeServiceImpl extends BaseCRUDService<Home, UUID, HomeRepository>
                            implements HomeService {

    private final ProductService productService;

    public HomeServiceImpl(HomeRepository homeRepository, ProductService productService) {
        super(homeRepository);
        this.productService = productService;
    }

    //region FIND

    public List<Home> findAllByUser(User user) {
        return repository.findByUsers_User(user);
    }

    public List<ShoppingList> findAllShoppingListsByHomeId(UUID homeId) {
        return repository.findById(homeId)
                .orElseThrow(() -> new NotFoundException("Home not found with id: " + homeId))
                .getShoppingLists();
    }

    public List<ProductStockHome> findAllProductStockHomeByHomeId(UUID homeId) {
        return repository.findById(homeId)
                .orElseThrow(() -> new NotFoundException("Home not found with id: " + homeId))
                .getStocks();
    }

    public List<UserHome> findAllUserHomeByHomeId(UUID homeId) {
        return repository.findById(homeId)
                .orElseThrow(() -> new NotFoundException("Home not found with id: " + homeId))
                .getUsers();
    }

    //endregion

    //region CREATE / ADD

    @Transactional
    public void addProductStock(UUID homeId, ProductStockHome request){
        Home home = repository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        home.addProductStock(request);
        repository.save(home);
    }

    @Transactional
    public void addUserHome(UUID homeId, UserHome request){
        Home home = repository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        home.addUserHome(request);
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
        ProductStockHome stock = home.getStocks().stream()
                .filter(s -> s.getProduct().getName().equals(product.getName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not found in home stock: " + product.getName()));
        stock.setQuantity(stock.getQuantity() - request.getQuantity());
        if (stock.getQuantity() <= 0) {
            home.getStocks().remove(stock);
        }
        repository.save(home);
    }

    @Transactional
    public void deleteUserHome(UUID homeId, UUID userId){
        Home home = repository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        UserHome userHome = home.getUsers().stream()
                .filter(u -> u.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found in home: " + userId));
        if (userHome.getHomeRole() == HomeRole.OWNER) {
            throw new ConflictException("Le propriétaire de la maison ne peut pas être retiré.");
        }
        home.getUsers().remove(userHome);
        repository.save(home);
    }

    //endregion

    //region ROLES

    /**
     * Transfers the OWNER role of a home to another existing member.
     * The current owner(s) are demoted to USER so there is always exactly one OWNER.
     * Caller must already be the owner (enforced at the controller via @homeSecurity.isOwner).
     */
    @Transactional
    public void transferOwnership(UUID homeId, UUID newOwnerUserId){
        Home home = repository.findById(homeId).orElseThrow(
                ()->new NotFoundException("Home not found with the id : " + homeId)
        );
        UserHome newOwner = home.getUsers().stream()
                .filter(u -> u.getUser().getId().equals(newOwnerUserId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found in home: " + newOwnerUserId));
        if (newOwner.getHomeRole() == HomeRole.OWNER) {
            throw new ConflictException("Cet utilisateur est déjà propriétaire de la maison.");
        }
        home.getUsers().stream()
                .filter(u -> u.getHomeRole() == HomeRole.OWNER)
                .forEach(u -> u.setHomeRole(HomeRole.USER));
        newOwner.setHomeRole(HomeRole.OWNER);
        repository.save(home);
    }

    //endregion
}
