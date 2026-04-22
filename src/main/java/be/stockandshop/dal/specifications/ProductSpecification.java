package be.stockandshop.dal.specifications;

import be.stockandshop.dal.entities.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> nameContains(String partialName) {

        return(root, query, cb) -> {
                if(partialName == null ) return null;
                return cb.like(cb.lower(root.get("name")), "%" + partialName.toLowerCase() + "%");
        };
    }
}
