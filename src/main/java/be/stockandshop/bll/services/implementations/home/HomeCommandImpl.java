package be.stockandshop.bll.services.implementations.home;

import be.stockandshop.bll.services.interfaces.home.HomeCommandService;
import be.stockandshop.pl.dto.reponses.HomeResponse;
import be.stockandshop.pl.dto.requests.HomeRequest;
import be.stockandshop.dal.entities.Home;
import be.stockandshop.dal.repositories.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class HomeCommandImpl implements HomeCommandService {

    private final HomeRepository homeRepository;

    public HomeResponse save(HomeRequest homeRequest) {
        if(homeRequest.getName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Home name is required");
        }
        return new HomeResponse(homeRepository.save(new Home(homeRequest.getName(), homeRequest.getDescription())));
    }

    public void delete(Long id) {
        Home home =  homeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Home id is required")
        );
        homeRepository.delete(home);
    }
}
