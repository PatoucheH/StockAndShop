package be.stockandshop.bll.services.interfaces.product;

import be.stockandshop.pl.dto.reponses.ProductResponse;

import java.util.List;

public interface ProductQueryService {
    ProductResponse findById(Long id);
    List<ProductResponse> findAll();
    List<ProductResponse> findProductByName(String name);
}
