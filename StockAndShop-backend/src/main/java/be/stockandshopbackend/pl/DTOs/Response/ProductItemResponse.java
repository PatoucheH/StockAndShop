package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ProductStockHome;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/// DTO for ProductStockHome AND ProductListItem return

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

    public ProductItemResponse(Long id, String name, String value, int quantity, boolean b) {
        this.id = id;
        this.nameProduct = name;
        this.unityProduct = value;
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

    public static ProductItemResponse fromProductStockHome(ProductStockHome productStockHome){
        return new ProductItemResponse(
                productStockHome.getId(),
                productStockHome.getProduct().getName(),
                productStockHome.getProduct().getUnity().getValue(),
                productStockHome.getQuantity(),
                false
        );
    }
}
