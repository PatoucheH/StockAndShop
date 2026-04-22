package be.stockandshop.pl.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HomeRequest {

    @NotBlank(message = "Home name is required")
    private String name;
    private String description;
}
