package be.stockandshopbackend.dal.repositories;

import be.stockandshopbackend.dl.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findProductsByNameContaining(String name);

    Optional<Product> findByName(String name);

    boolean existsByName(String name);

}
