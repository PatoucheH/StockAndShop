package be.stockandshopbackend.pl.DTOs.Response.home;

import be.stockandshopbackend.dl.entities.home.HomeExpense;


import java.util.List;

public record HomeExpenseResponse(
        String name,
        int amount,
        List<String> userConcernedName,
        String payerName
) {
    public static HomeExpenseResponse fromHomeExpense(HomeExpense he){
        return new HomeExpenseResponse(
                he.getName(),
                he.getAmount(),
                he.getUsersConcerned().stream()
                        .map(u -> u.getUser().getDisplayName())
                        .toList(),
                he.getPayer().getUser().getDisplayName()
        );
    }
}
