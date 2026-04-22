package be.stockandshop.bll.services.interfaces.productListLine;

import be.stockandshop.pl.dto.reponses.ProductListLineResponse;
import be.stockandshop.pl.dto.requests.ProductListLineRequest;

public interface ProductListLineCommandService {
    ProductListLineResponse save(ProductListLineRequest productListLine);
}
