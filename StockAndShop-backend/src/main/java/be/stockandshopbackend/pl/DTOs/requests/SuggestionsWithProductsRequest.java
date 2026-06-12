package be.stockandshopbackend.pl.DTOs.requests;

import java.util.List;

public record SuggestionsWithProductsRequest(List<String> productNames) {}
