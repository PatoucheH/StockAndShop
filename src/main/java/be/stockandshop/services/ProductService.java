package be.stockandshop.services;

import be.stockandshop.dto.reponses.ProductResponse;
import be.stockandshop.dto.requests.ProductRequest;
import be.stockandshop.entities.Product;
import be.stockandshop.repositories.ProductRepository;
import be.stockandshop.specifications.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .toList();
    }

    public ProductResponse save(ProductRequest product) {
        if(product.getName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name is required");
        }
        return new ProductResponse(productRepository.save(new Product(product.getName())));
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::new)
                .orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with id " + id)
        );
    }

    public List<ProductResponse> findProductByName(String name) {
        return productRepository.findAll(ProductSpecification.nameContains(name))
                .stream()
                .map(ProductResponse::new)
                .toList();
    }

    public void deleteById(Long id) {
       Product product = productRepository.findById(id).orElseThrow(
               () -> new ResponseStatusException(
                       HttpStatus.NOT_FOUND,
                       "Product not found with id " + id)
       );
        productRepository.delete(product);
    }

}
