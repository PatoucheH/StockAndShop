package be.stockandshopbackend.bll.services.userFavorite;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.ShoppingListRepository;
import be.stockandshopbackend.dal.repositories.UserFavoriteShoppingListRepository;
import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.entities.UserFavoriteShoppingList;
import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserFavoriteShoppingListServiceImpl
        extends BaseCRUDService<UserFavoriteShoppingList, Long, UserFavoriteShoppingListRepository>
        implements UserFavoriteShoppingListService {

    private final ShoppingListRepository shoppingListRepository;

    public UserFavoriteShoppingListServiceImpl(UserFavoriteShoppingListRepository favoriteRepository,
                                               ShoppingListRepository shoppingListRepository) {
        super(favoriteRepository);
        this.shoppingListRepository = shoppingListRepository;
    }

    public List<ShoppingList> getFavorites(User user) {
        return repository.findByUser(user).stream()
                .map(UserFavoriteShoppingList::getShoppingList)
                .toList();
    }

    @Transactional
    public void addFavorite(Long shoppingListId, User user) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId).orElseThrow(
                () -> new NotFoundException("Shopping list not found with id: " + shoppingListId)
        );
        if (repository.existsByUserAndShoppingList(user, shoppingList)) {
            throw new IllegalStateException("Shopping list already in favorites");
        }
        save(new UserFavoriteShoppingList(user, shoppingList));
    }

    @Transactional
    public void removeFavorite(Long shoppingListId, User user) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId).orElseThrow(
                () -> new NotFoundException("Shopping list not found with id: " + shoppingListId)
        );
        repository.deleteByUserAndShoppingList(user, shoppingList);
    }
}
