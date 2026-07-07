package be.stockandshopbackend.pl.controllers.home;

import be.stockandshopbackend.bll.services.home.HomeService;
import be.stockandshopbackend.bll.services.home.homeExpenses.HomeExpensesService;
import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.home.HomeExpense;
import be.stockandshopbackend.dl.entities.home.UserHome;
import be.stockandshopbackend.exceptions.ConflictException;
import be.stockandshopbackend.pl.DTOs.Response.home.HomeExpenseResponse;
import be.stockandshopbackend.pl.DTOs.Response.home.HomeResponse;
import be.stockandshopbackend.pl.DTOs.requests.home.HomeExpenseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home-expense")
@RequiredArgsConstructor
public class HomeExpensesController {

    private final HomeExpensesService homeExpensesService;
    private final HomeService homeService;

    @GetMapping("/{homeId}")
    @PreAuthorize("@homeSecurity.isInHome(#homeId, authentication.principal)")
    public ResponseEntity<List<HomeExpenseResponse>> getHomeExpenses(@PathVariable UUID homeId){
        return ResponseEntity.ok(
                homeExpensesService.findByHomeId(homeId).stream()
                        .map(HomeExpenseResponse::fromHomeExpense)
                        .toList()
        );
    }

    @PostMapping("/")
    @PreAuthorize("@homeSecurity.isInHome(#heRequest.homeId(), authentication.principal)")
    public ResponseEntity<?> createHomeResponse(@RequestBody @Valid HomeExpenseRequest heRequest ){
        Home home = homeService.findById(heRequest.homeId());
        UserHome payer = home.getUsers().stream()
                .filter(u -> u.getUser().getId().equals(heRequest.payerId()))
                .findFirst()
                .orElseThrow(() -> new ConflictException("Payer is not a member of this home"));
        Set<UserHome> userConcerned = home.getUsers().stream()
                .filter(u -> heRequest.userConcernedId().contains(u.getUser().getId()))
                .collect(Collectors.toSet());
        if (userConcerned.size() != heRequest.userConcernedId().size()) {
            throw new ConflictException("One or more concerned users are not members of this home");
        }
        HomeExpense he = new HomeExpense(
                heRequest.name(), heRequest.amount(), home, payer, userConcerned.stream().toList()
        );
        homeExpensesService.createHomeExpense(he);
        return ResponseEntity.ok().build();
    }
}
