package be.stockandshopbackend.dal.repositories.home;

import be.stockandshopbackend.dl.entities.product.ProductStockHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStockHomeRepository extends JpaRepository<ProductStockHome, Long> {
}
