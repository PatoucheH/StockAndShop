package be.stockandshopbackend.dal.repositories.product;

import be.stockandshopbackend.dl.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findProductsByNameContaining(String name);

    Optional<Product> findByName(String name);

    Optional<Product> findByBarcode(String barcode);

    boolean existsByName(String name);

}
