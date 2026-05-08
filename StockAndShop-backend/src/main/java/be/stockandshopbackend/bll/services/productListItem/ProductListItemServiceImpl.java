package be.stockandshopbackend.bll.services.productListItem;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.ProductListItemRepository;
import be.stockandshopbackend.dl.entities.ProductListItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.stockandshopbackend.exceptions.NotFoundException;

@Service
public class ProductListItemServiceImpl extends BaseCRUDService<ProductListItem, Long, ProductListItemRepository>
                                        implements ProductListItemService {

    public ProductListItemServiceImpl(ProductListItemRepository repository) {
        super(repository);
    }

    @Transactional
    public void checkedItem(Long productListItemId) {
        ProductListItem productListItem = repository.findById(productListItemId)
                .orElseThrow(() -> new NotFoundException("ProductListItem not found with id: " + productListItemId));
        productListItem.toggleIsChecked();
        repository.save(productListItem);
    }

}
