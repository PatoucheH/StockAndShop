package be.stockandshop.pl.controllers;

import be.stockandshop.bll.services.interfaces.product.ProductCommandService;
import be.stockandshop.bll.services.interfaces.product.ProductQueryService;
import be.stockandshop.pl.dto.reponses.ProductResponse;
import be.stockandshop.pl.dto.requests.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductQueryService productQueryService;
    private final ProductCommandService productCommandService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(@RequestParam(required = false) String name) {
        if (name != null) {
            return ResponseEntity.ok(productQueryService.findProductByName(name));
        }
        return ResponseEntity.ok(productQueryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productQueryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productCommandService.save(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducts(@PathVariable Long id) {
        productCommandService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
