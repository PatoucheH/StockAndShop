package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.ProductListItem;
import be.stockandshopbackend.dl.entities.ProductStockHome;
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
    private boolean isChecked;

    public static ProductItemResponse fromProductListItem(ProductListItem productListItem){
        return new ProductItemResponse(
                productListItem.getId(),
                productListItem.getProduct().getName(),
                productListItem.getProduct().getUnity().getValue(),
                productListItem.getQuantity(),
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
