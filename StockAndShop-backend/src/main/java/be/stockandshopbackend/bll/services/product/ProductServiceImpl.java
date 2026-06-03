package be.stockandshopbackend.bll.services.product;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.bll.services.category.CategoryService;
import be.stockandshopbackend.dal.repositories.ProductRepository;
import be.stockandshopbackend.dl.entities.Category;
import be.stockandshopbackend.dl.entities.Product;
import be.stockandshopbackend.dl.enums.Unity;
import be.stockandshopbackend.exceptions.AlreadyExistsException;
import be.stockandshopbackend.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends BaseCRUDService<Product, Long, ProductRepository>
                                implements ProductService {

    private final CategoryService categoryService;

    public ProductServiceImpl(ProductRepository repository, CategoryService categoryService) {
        super(repository);
        this.categoryService = categoryService;
    }

    public List<Product> findAllByName(String name){
        return  repository.findProductsByNameContaining(name);
    }

    public Product findOneByName(String name){
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Product not found with name : " + name));
    }

    public Unity[] findAllUnities(){
        return Unity.values();
    }

    @Transactional
    public Product createProduct(String name, String unity, String categoryName){
        if(repository.existsByName(name)) throw new AlreadyExistsException("This product exists already");
        Category category = categoryService.findByNameOrCreate(categoryName);
        Product product = new Product();
        product.setName(name.toLowerCase());
        product.setUnity(Unity.fromValue(unity));
        product.setCategory(category);
        return repository.save(product);
    }
}
