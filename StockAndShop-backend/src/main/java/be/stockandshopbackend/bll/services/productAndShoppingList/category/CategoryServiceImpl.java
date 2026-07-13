package be.stockandshopbackend.bll.services.productAndShoppingList.category;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.product.CategoryRepository;
import be.stockandshopbackend.dl.entities.product.Category;
import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl extends BaseCRUDService<Category, Long, CategoryRepository>
                                implements CategoryService {

    protected CategoryServiceImpl(CategoryRepository repository) {
        super(repository);
    }

    public Category findByNameOrCreate(String name){
        return repository.findByNameIgnoreCase(name)
                .orElseGet(() -> repository.save(new Category(name)));
    }

    @Transactional
    public Category updateCategory(Long id, String name, String description){
        Category category = repository.findById(id).orElseThrow(
                () ->  new NotFoundException("Category with id " + id + " not found")
        );
        category.setName(name);
        category.setDescription(description);
        return repository.save(category);
    }
}
