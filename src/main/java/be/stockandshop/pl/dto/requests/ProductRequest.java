package be.stockandshop.pl.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class ProductRequest {

     @NotBlank(message = "Product name is required")
    private String name;
}
