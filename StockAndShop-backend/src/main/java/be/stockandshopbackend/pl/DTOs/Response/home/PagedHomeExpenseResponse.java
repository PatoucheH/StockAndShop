package be.stockandshopbackend.pl.DTOs.Response.home;

import java.util.List;

public record PagedHomeExpenseResponse(
        List<HomeExpenseResponse> expenses,
        long total,
        int page,
        int size,
        boolean hasMore
) {}
