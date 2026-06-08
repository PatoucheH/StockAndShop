package be.stockandshopbackend.dal.repositories;

import be.stockandshopbackend.dl.entities.ShoppingList;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.dl.entities.UserFavoriteShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteShoppingListRepository extends JpaRepository<UserFavoriteShoppingList, Long> {
    List<UserFavoriteShoppingList> findByUser(User user);
    boolean existsByUserAndShoppingList(User user, ShoppingList shoppingList);
    void deleteByUserAndShoppingList(User user, ShoppingList shoppingList);
}
