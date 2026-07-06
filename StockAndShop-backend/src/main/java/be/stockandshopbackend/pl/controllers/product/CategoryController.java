package be.stockandshopbackend.pl.controllers.product;

import be.stockandshopbackend.bll.services.productAndShoppingList.category.CategoryService;
import be.stockandshopbackend.pl.DTOs.Response.products.CategoryResponse;
import be.stockandshopbackend.pl.DTOs.requests.products.CategoryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    //region GET

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll().stream()
                .map(CategoryResponse::fromCategory)
                .toList());
    }

    //endregion

    //region PUT

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody @Valid CategoryRequest categoryRequest) {
        return ResponseEntity.ok(
                CategoryResponse.fromCategory(
                        categoryService.updateCategory(id,categoryRequest)
                )
        );
    }

    //endregion
}
