package be.stockandshopbackend.bll.services.productAndShoppingList.category;

import be.stockandshopbackend.dl.entities.product.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();
    Category findByNameOrCreate(String name);
    Category updateCategory(Long id, String name, String description);
}
