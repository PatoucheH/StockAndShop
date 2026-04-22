package be.stockandshop.dal.repositories;

import be.stockandshop.dal.entities.ProductStockLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStockLineRepository extends JpaRepository<ProductStockLine, Long>, JpaSpecificationExecutor<ProductStockLine> {
}
