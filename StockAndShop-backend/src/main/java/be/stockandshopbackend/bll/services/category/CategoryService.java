package be.stockandshopbackend.bll.services.category;

import be.stockandshopbackend.dl.entities.Category;
import be.stockandshopbackend.pl.DTOs.requests.CategoryRequest;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();
    Category findByNameOrCreate(String name);
    Category updateCategory(Long id, CategoryRequest request);
}
