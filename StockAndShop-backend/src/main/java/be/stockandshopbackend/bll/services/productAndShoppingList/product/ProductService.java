package be.stockandshopbackend.bll.services.productAndShoppingList.product;

import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.enums.Unity;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ///GET
    List<Product> findAll();
    Product findById(Long id);
    List<Product> findAllByName(String name);
    Product findOneByName(String name);
    Unity[] findAllUnities();
    Optional<Product> findByBarcode(String barcode);

    /// ADD
    Product createProduct(String name, String unity, String categoryName);
    Product save(Product product);

    /// DELETE
    void deleteById(Long id);
}
