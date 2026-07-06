package be.stockandshopbackend.dal.repositories.product;

import be.stockandshopbackend.dl.entities.product.ProductListItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductListItemRepository extends JpaRepository<ProductListItem, Long> {

    // Native query because ProductListItem has no back-reference to ShoppingList, preventing Spring from deriving this join
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_list_item WHERE id = :itemId AND shopping_list_id = :listId",
            nativeQuery = true)
    void deleteByIdAndShoppingListId(@Param("itemId") int itemId, @Param("listId") Long listId);
}
