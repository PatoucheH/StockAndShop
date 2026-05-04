package be.stockandshopbackend.bll.services;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.ProductRepository;
import be.stockandshopbackend.dl.entities.Category;
import be.stockandshopbackend.dl.entities.Product;
import be.stockandshopbackend.dl.enums.Unity;
import be.stockandshopbackend.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService extends BaseCRUDService<Product, Long, ProductRepository> {

    private final CategoryService categoryService;

    public ProductService(ProductRepository repository, CategoryService categoryService) {
        super(repository);
        this.categoryService = categoryService;
    }

    public List<Product> findAllByName(String name){
        return  repository.findProductsByNameContaining(name);
    }

    @Transactional
    public Product createProduct(String name, String unity, String categoryName){
        Category category = categoryService.findByNameOrCreate(categoryName);
        Product product = new Product();
        product.setName(name);
        product.setUnity(Unity.fromValue(unity));
        product.setCategory(category);
        return repository.save(product);
    }

    public Product findOneByName(String name){
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Product not found with name : " + name));
    }

}
