package be.stockandshopbackend.dl.entities.product;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import be.stockandshopbackend.dl.enums.Unity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Product extends LongBaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unity unity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Category category;

    @Column(unique = true)
    private String barcode;

    private String brand;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private String packageQuantity;

    private String nutriscoreGrade;

    private String ecoscoreGrade;

}
