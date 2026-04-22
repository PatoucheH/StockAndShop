package be.stockandshop.bll.services.implementations.product;

import be.stockandshop.bll.services.interfaces.product.ProductCommandService;
import be.stockandshop.dal.entities.Product;
import be.stockandshop.dal.repositories.ProductRepository;
import be.stockandshop.pl.dto.reponses.ProductResponse;
import be.stockandshop.pl.dto.requests.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCommandImpl implements ProductCommandService {

    private final ProductRepository productRepository;

    public ProductResponse save(ProductRequest product) {
        return new ProductResponse(productRepository.save(new Product(product.getName())));
    }
    public void deleteById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product not found with id " + id));
        productRepository.delete(product);
    }
}
