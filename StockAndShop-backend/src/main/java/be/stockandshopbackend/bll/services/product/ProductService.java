package be.stockandshopbackend.bll.services.product;

import be.stockandshopbackend.dl.entities.Product;
import be.stockandshopbackend.dl.enums.Unity;

import java.util.List;

public interface ProductService {

    ///GET
    List<Product> findAll();
    Product findById(Long id);
    List<Product> findAllByName(String name);
    Product findOneByName(String name);
    Unity[] findAllUnities();

    /// ADD
    Product createProduct(String name, String unity, String categoryName);
    Product save(Product product);

    /// DELETE
    void deleteById(Long id);
}
