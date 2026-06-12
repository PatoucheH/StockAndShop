package be.stockandshopbackend.dal.repositories;

import be.stockandshopbackend.dl.entities.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> findByProductsId(Long productListItemId);
}
