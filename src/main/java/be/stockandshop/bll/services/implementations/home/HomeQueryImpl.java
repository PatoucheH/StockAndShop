package be.stockandshop.bll.services.implementations.home;

import be.stockandshop.bll.services.interfaces.home.HomeQueryService;
import be.stockandshop.dal.repositories.HomeRepository;
import be.stockandshop.pl.dto.reponses.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeQueryImpl implements HomeQueryService {

    private final HomeRepository homeRepository;

    public List<HomeResponse> findAllByUserId(UUID userId) {
        return homeRepository.findAllByUserId(userId)
                .stream()
                .map(HomeResponse::new)
                .toList();
    }
}
