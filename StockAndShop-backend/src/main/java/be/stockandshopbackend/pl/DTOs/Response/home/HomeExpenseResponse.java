package be.stockandshopbackend.pl.DTOs.Response.home;

import be.stockandshopbackend.dl.entities.home.HomeExpense;

import java.time.LocalDateTime;
import java.util.List;

public record HomeExpenseResponse(
        Long id,
        String name,
        int amount,
        List<String> userConcernedName,
        String payerName,
        String type,
        LocalDateTime createdAt
) {
    public static HomeExpenseResponse fromHomeExpense(HomeExpense he){
        return new HomeExpenseResponse(
                he.getId(),
                he.getName(),
                he.getAmount(),
                he.getUsersConcerned().stream()
                        .map(u -> u.getUser().getDisplayName())
                        .toList(),
                he.getPayer().getUser().getDisplayName(),
                he.getType().name(),
                he.getCreatedAt()
        );
    }
}
