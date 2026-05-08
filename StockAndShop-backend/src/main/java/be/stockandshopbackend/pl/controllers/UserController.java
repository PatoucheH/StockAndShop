package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.role.RoleService;
import be.stockandshopbackend.bll.services.userService.UserService;
import be.stockandshopbackend.pl.DTOs.Response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    //region GET

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll(){
        return ResponseEntity.ok(userService.findAll().stream()
                .map(UserResponse::fromUser)
                .toList()
        );
    }

    //endregion

    //region ADD / REMOVE

    @PutMapping("/{id}/add-role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addRoleToUser(@PathVariable UUID id, @RequestParam String roleName){
        roleService.addRoleToUser(id, roleName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/remove-role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable UUID id, @RequestParam String roleName){
        roleService.removeRoleToUser(id, roleName);
        return ResponseEntity.noContent().build();
    }

    //endregion
}
