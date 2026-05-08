package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.category.CategoryService;
import be.stockandshopbackend.bll.services.product.ProductService;
import be.stockandshopbackend.dl.entities.Category;
import be.stockandshopbackend.dl.entities.Product;
import be.stockandshopbackend.dl.enums.Unity;
import be.stockandshopbackend.pl.DTOs.Response.ProductResponse;
import be.stockandshopbackend.pl.DTOs.requests.ProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    //region GET

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll(
            @RequestParam(value = "name", required = false) String name
    ) {
        List<Product> products = (name != null && !name.isBlank())
                ? productService.findAllByName(name)
                : productService.findAll();
        return ResponseEntity.ok(products.stream()
                .map(ProductResponse::fromProduct)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ProductResponse.fromProduct(productService.findById(id)));
    }

    //endregion

    //region POST

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest p){
        Product product = productService.createProduct(p.name(), p.unity(), p.category());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.fromProduct(product));
    }

    //endregion

    //region DELETE

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //endregion

    //region PUT

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @RequestBody @Valid ProductRequest p){
        Product product = productService.findById(id);
        product.setName(p.name().toLowerCase());
        product.setUnity(Unity.fromValue(p.unity()));
        Category category = categoryService.findByNameOrCreate(p.category());
        product.setCategory(category);
        productService.save(product);
        return ResponseEntity.ok(ProductResponse.fromProduct(product));
    }

    //endregion
}
