package be.stockandshopbackend.dl.entities.product;

import be.stockandshopbackend.dl.entities.base.LongBaseEntity;
import be.stockandshopbackend.dl.enums.Unity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Product extends LongBaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    // Possible units for this product, ordered — the first one is the default.
    // Stored as a comma-separated string (e.g. "BOTTLE,CAN") via UnityListConverter.
    @Convert(converter = UnityListConverter.class)
    @Column(name = "unities", length = 255)
    private List<Unity> unities = new ArrayList<>();

    /** Backward-compatible accessor: the default (first) unit of the product. */
    public Unity getUnity() {
        return (unities == null || unities.isEmpty()) ? Unity.OTHER : unities.get(0);
    }

    /** Backward-compatible setter: replaces the unit list with a single unit. */
    public void setUnity(Unity unity) {
        this.unities = new ArrayList<>(List.of(unity));
    }

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
