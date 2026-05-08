package be.stockandshopbackend.bll.services.category;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.CategoryRepository;
import be.stockandshopbackend.dl.entities.Category;
import be.stockandshopbackend.exceptions.NotFoundException;
import be.stockandshopbackend.pl.DTOs.requests.CategoryRequest;
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
    public Category updateCategory(Long id, CategoryRequest request){
        Category category = repository.findById(id).orElseThrow(
                () ->  new NotFoundException("Category with id " + id + " not found")
        );
        category.setName(request.name());
        category.setDescription(request.description());
        return repository.save(category);
    }
}
