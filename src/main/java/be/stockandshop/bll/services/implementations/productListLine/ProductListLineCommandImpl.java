package be.stockandshop.bll.services.implementations.productListLine;

import be.stockandshop.bll.services.interfaces.productListLine.ProductListLineCommandService;
import be.stockandshop.pl.dto.reponses.ProductListLineResponse;
import be.stockandshop.pl.dto.requests.ProductListLineRequest;
import be.stockandshop.dal.entities.Product;
import be.stockandshop.dal.entities.ProductListLine;
import be.stockandshop.dal.repositories.ProductListLineRepository;
import be.stockandshop.dal.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductListLineCommandImpl implements ProductListLineCommandService {

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
