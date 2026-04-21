package be.stockandshop.services;

import be.stockandshop.dto.reponses.ProductListLineResponse;
import be.stockandshop.dto.requests.ProductListLineRequest;
import be.stockandshop.entities.Product;
import be.stockandshop.entities.ProductListLine;
import be.stockandshop.repositories.ProductListLineRepository;
import be.stockandshop.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductListLineService {

    private final ProductListLineRepository productListlineRepository;
    private final ProductRepository productRepository;

    public ProductListLineResponse save(ProductListLineRequest productListLine) {
        Product product = productRepository.findById(productListLine.getProductId())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found with id " + productListLine.getProductId())
                );
        return new ProductListLineResponse(productListlineRepository.save(
                new ProductListLine(product, productListLine.getQuantity())));
    }




}
