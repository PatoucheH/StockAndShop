package be.stockandshop.pl.dto.reponses;

import be.stockandshop.dal.entities.Product;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductResponse {

    private final Long id;
    private final String name;
    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;

    public ProductResponse(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.createAt = product.getCreatedAt();
        this.updateAt = product.getUpdatedAt();
    }

}
