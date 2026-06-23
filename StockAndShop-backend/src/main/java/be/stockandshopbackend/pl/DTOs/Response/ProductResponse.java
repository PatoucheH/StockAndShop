package be.stockandshopbackend.pl.DTOs.Response;

import be.stockandshopbackend.dl.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductResponse{
    private Long id;
    private String name;
    private String unity;
    private String category;
    private String barcode;
    private String brand;
    private String imageUrl;
    private String packageQuantity;
    private String nutriscoreGrade;
    private String ecoscoreGrade;


    public static ProductResponse fromProduct(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getUnity().getValue(),
                p.getCategory().getName(),
                p.getBarcode(),
                p.getBrand(),
                p.getImageUrl(),
                p.getPackageQuantity(),
                p.getNutriscoreGrade(),
                p.getEcoscoreGrade()
        );
    }
}
