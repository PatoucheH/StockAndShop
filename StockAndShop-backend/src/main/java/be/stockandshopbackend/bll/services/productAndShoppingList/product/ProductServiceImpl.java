package be.stockandshopbackend.bll.services.productAndShoppingList.product;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.bll.services.productAndShoppingList.category.CategoryService;
import be.stockandshopbackend.bll.services.openfoodfacts.OpenFoodFactsService;
import be.stockandshopbackend.dal.repositories.product.ProductRepository;
import be.stockandshopbackend.dl.entities.product.Category;
import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.enums.Unity;
import be.stockandshopbackend.exceptions.AlreadyExistsException;
import be.stockandshopbackend.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl extends BaseCRUDService<Product, Long, ProductRepository>
                                implements ProductService {

    private final CategoryService categoryService;
    private final OpenFoodFactsService openFoodFactsService;

    public ProductServiceImpl(ProductRepository repository, CategoryService categoryService,
                              OpenFoodFactsService openFoodFactsService) {
        super(repository);
        this.categoryService = categoryService;
        this.openFoodFactsService = openFoodFactsService;
    }

    public List<Product> findAllByName(String name){
        String term = name == null ? "" : name.replaceAll("[^\\p{L}\\p{N} ]", " ").trim();
        if (term.isEmpty()) return List.of();
        return repository.searchByName(term);
    }

    public Product findOneByName(String name){
        return repository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Produit introuvable avec le nom : " + name));
    }

    public Unity[] findAllUnities(){
        return Unity.values();
    }

    /**
     * Checks the local DB first; on a miss, calls OpenFoodFacts and persists the result
     * so subsequent scans of the same barcode are served locally.
     */
    public Optional<Product> findByBarcode(String barcode){
        return repository.findByBarcode(barcode)
                .or(() -> openFoodFactsService.fetchAndSave(barcode));
    }


    @Transactional
    public Product createProduct(String name, String unity, String categoryName){
        if(repository.existsByName(name)) throw new AlreadyExistsException("This product exists already");
        Category category = categoryService.findByNameOrCreate(categoryName);
        Product product = new Product();
        product.setName(name.trim());
        product.setUnity(Unity.fromValue(unity));
        product.setCategory(category);
        return repository.save(product);
    }
}
