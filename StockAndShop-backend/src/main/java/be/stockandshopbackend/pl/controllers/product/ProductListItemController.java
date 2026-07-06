package be.stockandshopbackend.pl.controllers.product;

import be.stockandshopbackend.bll.services.productAndShoppingList.productListItem.ProductListItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product-list-item")
@RequiredArgsConstructor
public class ProductListItemController {

    private final ProductListItemService productListItemService;

    @PatchMapping("/{id}/check")
    @PreAuthorize("@homeSecurity.isInHomeByProductItem(#id, authentication.principal)")
    public ResponseEntity<?> toggleCheckedProduct(@PathVariable Long id){
        productListItemService.checkedItem(id);
        return ResponseEntity.noContent().build();
    }

}
