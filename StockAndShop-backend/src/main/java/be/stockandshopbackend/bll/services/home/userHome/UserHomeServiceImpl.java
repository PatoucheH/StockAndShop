package be.stockandshopbackend.bll.services.home.userHome;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.home.UserHomeRepository;
import be.stockandshopbackend.dl.entities.home.UserHome;
import org.springframework.stereotype.Service;

@Service
public class UserHomeServiceImpl extends BaseCRUDService<UserHome, Long, UserHomeRepository>
                                implements UserHomeService {

    public UserHomeServiceImpl(UserHomeRepository userHomeRepository) {
        super(userHomeRepository);
    }
}
