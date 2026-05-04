package be.stockandshopbackend.dal.repositories;

import be.stockandshopbackend.dl.entities.Home;
import be.stockandshopbackend.dl.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HomeRepository extends JpaRepository<Home, UUID> {
    Optional<Home> findByShoppingListsId(Long shoppingListId);
    Optional<Home> findByShoppingLists_ProductsId(Long productListItemId);
    List<Home> findByUsers_User(User user);
}
