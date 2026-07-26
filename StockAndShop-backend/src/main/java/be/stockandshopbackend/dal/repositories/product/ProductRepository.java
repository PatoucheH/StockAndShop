package be.stockandshopbackend.dal.repositories.product;

import be.stockandshopbackend.dl.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
      value = "SELECT * FROM product WHERE f_unaccent(lower(name)) ~ " +
              "('(^|[^a-z0-9])' || f_unaccent(lower(:term))) " +
              "ORDER BY (f_unaccent(lower(name)) LIKE (f_unaccent(lower(:term)) || '%')) DESC, " +
              "length(name), name LIMIT 50",
      nativeQuery = true)
    List<Product> searchByName(@Param("term") String term);

    // Case-insensitive: names are stored with their original casing (e.g. "Pâtes Panzani"),
    // but callers may pass a lowercased name.
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
    Optional<Product> findByName(@Param("name") String name);

    Optional<Product> findByBarcode(String barcode);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE LOWER(p.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

}
