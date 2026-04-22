package be.stockandshop.pl.controllers;

import be.stockandshop.bll.services.interfaces.home.HomeCommandService;
import be.stockandshop.bll.services.interfaces.home.HomeQueryService;
import be.stockandshop.pl.dto.reponses.HomeResponse;
import be.stockandshop.pl.dto.requests.HomeRequest;
import be.stockandshop.bll.services.implementations.home.HomeCommandImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeCommandService homeCommand;
    private final HomeQueryService homeQuery;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody HomeRequest home) {
        homeCommand.save(home);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        homeCommand.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<HomeResponse>> getAllHomeByUser(@PathVariable UUID id) {
        return ResponseEntity.ok(homeQuery.findAllByUserId(id));
    }
}
