package be.stockandshopbackend.pl.DTOs.requests;

import be.stockandshopbackend.dl.entities.product.ProductStockHome;

import java.util.List;

public record GenerateRecipeRequest(
        List<ProductStockHome> products
) {
}