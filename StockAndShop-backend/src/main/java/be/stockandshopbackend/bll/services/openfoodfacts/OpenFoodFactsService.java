package be.stockandshopbackend.bll.services.openfoodfacts;

import be.stockandshopbackend.dl.entities.product.Product;

import java.util.Optional;

public interface OpenFoodFactsService {
    Optional<Product> fetchAndSave(String barcode);
}
