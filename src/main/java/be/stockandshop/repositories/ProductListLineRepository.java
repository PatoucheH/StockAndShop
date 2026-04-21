package be.stockandshop.repositories;

import be.stockandshop.entities.ProductListLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductListLineRepository extends JpaRepository<ProductListLine, Long> {
}
