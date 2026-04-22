package be.stockandshop.bll.services.interfaces.home;

import be.stockandshop.pl.dto.reponses.HomeResponse;
import be.stockandshop.pl.dto.requests.HomeRequest;

public interface HomeCommandService {

    HomeResponse save(HomeRequest homeRequest);
    void delete(Long id);
}
