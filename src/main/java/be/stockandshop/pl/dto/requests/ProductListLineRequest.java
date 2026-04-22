package be.stockandshop.pl.dto.requests;

import lombok.Data;

@Data
public class ProductListLineRequest {

    private final Long productId;
    private final Integer quantity;
}
