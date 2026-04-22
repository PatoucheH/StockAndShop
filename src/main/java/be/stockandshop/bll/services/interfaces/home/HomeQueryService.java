package be.stockandshop.bll.services.interfaces.home;

import be.stockandshop.pl.dto.reponses.HomeResponse;

import java.util.List;
import java.util.UUID;

public interface HomeQueryService {

    List<HomeResponse> findAllByUserId(UUID userId);
}
