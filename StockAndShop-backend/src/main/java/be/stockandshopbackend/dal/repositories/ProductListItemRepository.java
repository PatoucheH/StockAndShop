package be.stockandshopbackend.dal.repositories;

import be.stockandshopbackend.dl.entities.ProductListItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductListItemRepository extends JpaRepository<ProductListItem, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_list_item WHERE shopping_list_id = :listId " +
                   "AND product_id = (SELECT id FROM product WHERE LOWER(name) = LOWER(:productName))",
            nativeQuery = true)
    void deleteByShoppingListIdAndProductName(@Param("listId") Long listId, @Param("productName") String productName);
}
