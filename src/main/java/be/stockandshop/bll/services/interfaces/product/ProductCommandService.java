package be.stockandshop.bll.services.interfaces.product;

import be.stockandshop.pl.dto.reponses.ProductResponse;
import be.stockandshop.pl.dto.requests.ProductRequest;

public interface ProductCommandService {
    ProductResponse save(ProductRequest product);
    void deleteById(Long id);
}
