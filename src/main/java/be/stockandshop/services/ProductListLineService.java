package be.stockandshop.services;

import be.stockandshop.entities.ProductListLine;
import be.stockandshop.repositories.ProductListlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductListLineService {

    private final ProductListlineRepository productListlineRepository;

    public ProductListLine findById(Long id) {
        return productListlineRepository.findById(id).orElse(null);
    }

    public ProductListLine save(ProductListLine productListLine) {
        return productListlineRepository.save(productListLine);
    }

    public void delete(ProductListLine productListLine) {
        productListlineRepository.delete(productListLine);
    }
}
