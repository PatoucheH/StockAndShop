package be.stockandshopbackend.pl.controllers.product;

import be.stockandshopbackend.bll.services.openfoodfacts.OpenFoodFactsService;
import be.stockandshopbackend.bll.services.productAndShoppingList.category.CategoryService;
import be.stockandshopbackend.bll.services.productAndShoppingList.product.ProductService;
import be.stockandshopbackend.dl.entities.product.Category;
import be.stockandshopbackend.dl.entities.product.Product;
import be.stockandshopbackend.dl.enums.Unity;
import be.stockandshopbackend.pl.DTOs.Response.products.ProductResponse;
import be.stockandshopbackend.pl.DTOs.Response.products.ProductSearchResponse;
import be.stockandshopbackend.pl.DTOs.requests.products.ProductRequest;
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
    private final OpenFoodFactsService openFoodFactsService;

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

    // Lightweight autocomplete search: returns only id/name/unity/category to minimize payload.
    @GetMapping("/search")
    public ResponseEntity<List<ProductSearchResponse>> search(
            @RequestParam(value = "name", required = false) String name
    ) {
        if (name == null || name.isBlank()) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(productService.findAllByName(name).stream()
                .map(ProductSearchResponse::fromProduct)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ProductResponse.fromProduct(productService.findById(id)));
    }

    @GetMapping("/unity")
    public ResponseEntity<Unity[]> getUnities(){
        return ResponseEntity.ok(productService.findAllUnities());
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponse> getProductByBarcode(@PathVariable String barcode){
        // 1) déjà en base ? 2) sinon fallback Open Food Facts (crée le produit avec
        //    la bonne catégorie interne + la liste d'unités) ; 3) sinon 404.
        return productService.findByBarcode(barcode)
                .or(() -> openFoodFactsService.fetchAndSave(barcode))
                .map(ProductResponse::fromProduct)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        product.setName(p.name().trim());
        product.setUnity(Unity.fromValue(p.unity()));
        Category category = categoryService.findByNameOrCreate(p.category());
        product.setCategory(category);
        productService.save(product);
        return ResponseEntity.ok(ProductResponse.fromProduct(product));
    }

    //endregion
}
