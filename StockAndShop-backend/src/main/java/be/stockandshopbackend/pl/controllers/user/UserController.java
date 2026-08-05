package be.stockandshopbackend.pl.controllers.user;

import be.stockandshopbackend.bll.services.user.role.RoleService;
import be.stockandshopbackend.bll.services.user.userService.UserService;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.pl.DTOs.Response.user.UserResponse;
import be.stockandshopbackend.pl.DTOs.requests.user.ChangePasswordRequest;
import be.stockandshopbackend.pl.DTOs.requests.user.UpdateUsernameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> search(@RequestParam String query){
        return ResponseEntity.ok(userService.searchByQuery(query).stream()
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

    // Updates the connected user's display name
    @PutMapping("/me/username")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMyUsername(@AuthenticationPrincipal User user,
                                              @RequestBody @Valid UpdateUsernameRequest request){
        userService.updateUsername(user, request.username());
        return ResponseEntity.noContent().build();
    }

    // Changes the connected user's password (requires the current one)
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeMyPassword(@AuthenticationPrincipal User user,
                                              @RequestBody @Valid ChangePasswordRequest request){
        userService.changePassword(user, request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    // Deletes the connected user's own account (blocked if owner of a home or non-zero balance)
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMyAccount(@AuthenticationPrincipal User user){
        userService.deleteAccount(user);
        return ResponseEntity.noContent().build();
    }

    //endregion
}
