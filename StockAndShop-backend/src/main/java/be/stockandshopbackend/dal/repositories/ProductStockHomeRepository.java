package be.stockandshopbackend.dal.repositories;

import be.stockandshopbackend.dl.entities.ProductStockHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStockHomeRepository extends JpaRepository<ProductStockHome, Long> {
}
