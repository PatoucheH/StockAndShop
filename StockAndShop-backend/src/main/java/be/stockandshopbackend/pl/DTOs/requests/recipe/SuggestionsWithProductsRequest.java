package be.stockandshopbackend.pl.DTOs.requests.recipe;

import java.util.List;

public record SuggestionsWithProductsRequest(List<String> productNames) {}
