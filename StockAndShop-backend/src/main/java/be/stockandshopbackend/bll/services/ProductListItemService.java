package be.stockandshopbackend.bll.services;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.ProductListItemRepository;
import be.stockandshopbackend.dl.entities.ProductListItem;
import org.springframework.stereotype.Service;

@Service
public class ProductListItemService extends BaseCRUDService<ProductListItem, Long, ProductListItemRepository> {

    public ProductListItemService(ProductListItemRepository repository) {
        super(repository);
    }

}
