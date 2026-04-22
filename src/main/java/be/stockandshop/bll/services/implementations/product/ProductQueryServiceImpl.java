package be.stockandshop.bll.services.implementations.product;

import be.stockandshop.bll.services.interfaces.product.ProductQueryService;
import be.stockandshop.pl.dto.reponses.ProductResponse;
import be.stockandshop.dal.repositories.ProductRepository;
import be.stockandshop.dal.specifications.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .toList();
    }


    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::new)
                .orElseThrow(
                () -> new EntityNotFoundException("Product not found with id " + id));
    }

    public List<ProductResponse> findProductByName(String name) {
        return productRepository.findAll(ProductSpecification.nameContains(name))
                .stream()
                .map(ProductResponse::new)
                .toList();
    }


}
