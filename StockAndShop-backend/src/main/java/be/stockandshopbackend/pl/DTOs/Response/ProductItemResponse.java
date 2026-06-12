package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ProductStockHome;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Shared DTO for both ProductStockHome (stock) and ProductListItem (shopping list)

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductItemResponse {
    private Long id;
    private String nameProduct;
    private String unityProduct;
    private int quantity;
    private String category;
    @JsonProperty("isChecked")
    private boolean isChecked;

    public ProductItemResponse(Long id, String name, String unity, String category, int quantity, boolean b) {
        this.id = id;
        this.nameProduct = name;
        this.unityProduct = unity;
        this.category = category;
        this.quantity = quantity;
        this.isChecked = b;
    }

    public static ProductItemResponse fromProductListItem(ProductListItem productListItem){
        return new ProductItemResponse(
                productListItem.getId(),
                productListItem.getProduct().getName(),
                productListItem.getProduct().getUnity().getValue(),
                productListItem.getQuantity(),
                productListItem.getProduct().getCategory().getName(),
                productListItem.isChecked()
        );
    }

    // Stock items are never in a checked state
    public static ProductItemResponse fromProductStockHome(ProductStockHome productStockHome){
        return new ProductItemResponse(
                productStockHome.getId(),
                productStockHome.getProduct().getName(),
                productStockHome.getProduct().getUnity().getValue(),
                productStockHome.getProduct().getCategory().getName(),
                productStockHome.getQuantity(),
                false
        );
    }
}
