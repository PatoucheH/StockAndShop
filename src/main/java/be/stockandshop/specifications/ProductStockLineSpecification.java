package be.stockandshop.specifications;

import be.stockandshop.entities.ProductStockLine;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ProductStockLineSpecification {

    public static Specification<ProductStockLine> byHomeId(Long homeId) {
        return (root, query, cb) ->
                homeId == null ? null : cb.equal(root.get("home").get("id"), homeId);
    }

    public static Specification<ProductStockLine> byProductNameContaining(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            Join<Object, Object> product = root.join("product");
            return cb.like(cb.lower(product.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<ProductStockLine> belowQuantity(Integer threshold) {
        return (root, query, cb) ->
                threshold == null ? null : cb.lessThanOrEqualTo(root.get("quantity"), threshold);
    }
}
