package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.RoleService;
import be.stockandshopbackend.dal.repositories.RoleRepository;
import be.stockandshopbackend.dl.entities.Role;
import be.stockandshopbackend.pl.DTOs.Response.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    //region GET

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RoleResponse>> getAllRoles(){
        return ResponseEntity.ok(roleService.findAll().stream()
                .map(RoleResponse::fromRole)
                .toList());
    }

    //endregion

    //region CREATE

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoleResponse> createRole(@RequestParam String roleName){
        Role role = roleService.createRole(roleName);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponse.fromRole(role));
    }

    //endregion
}
