package be.stockandshopbackend.bll.services.security;

import be.stockandshopbackend.dal.repositories.RoleRepository;
import be.stockandshopbackend.dal.repositories.UserRepository;
import be.stockandshopbackend.dl.entities.Role;
import be.stockandshopbackend.dl.entities.User;
import be.stockandshopbackend.exceptions.ConflictException;
import be.stockandshopbackend.pl.DTOs.Response.AuthResponse;
import be.stockandshopbackend.pl.DTOs.requests.LoginRequest;
import be.stockandshopbackend.pl.DTOs.requests.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));
    }

    public AuthResult register(RegisterRequest registerRequest) {
        if(userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new ConflictException("Email already in use: " + registerRequest.email());
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));
        User user = new User(
                registerRequest.username(),
                registerRequest.email(),
                passwordEncoder.encode(registerRequest.password()),
                Set.of(userRole)
        );
        userRepository.save(user);
        return buildAuthResult(user);
    }

    public AuthResult login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + loginRequest.email()));
        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }
        return buildAuthResult(user);
    }

    public String refreshAccessToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        UserDetails user = loadUserByUsername(email);
        if(!jwtService.validateToken(refreshToken, user)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
        return jwtService.generateToken(user);
    }

    private AuthResult buildAuthResult(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getAuthority)
                .toList();
        return new AuthResult(
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user),
                user.getUsername(),
                user.getDisplayName(),
                roles
        );
    }
}
