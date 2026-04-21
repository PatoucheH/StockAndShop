package be.stockandshop.dto.reponses;

import be.stockandshop.entities.Product;
import be.stockandshop.entities.ProductListLine;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductListLineResponse {

    private final Long id;
    private final String productName;
    private final Integer quantity;
    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;

    public ProductListLineResponse(ProductListLine product) {
        this.id = product.getId();
        this.productName = product.getProduct().getName();
        this.quantity = product.getQuantity();
        this.createAt = product.getCreatedAt();
        this.updateAt = product.getUpdatedAt();

    }

}
