package be.stockandshopbackend.bll.services.security.services;

import be.stockandshopbackend.bll.services.security.records.AuthResult;
import be.stockandshopbackend.bll.services.security.records.RefreshResult;
import be.stockandshopbackend.dal.repositories.user.RefreshTokenRepository;
import be.stockandshopbackend.dal.repositories.user.RoleRepository;
import be.stockandshopbackend.dal.repositories.user.UserRepository;
import be.stockandshopbackend.dl.entities.user.RefreshToken;
import be.stockandshopbackend.dl.entities.user.Role;
import be.stockandshopbackend.dl.entities.user.User;
import be.stockandshopbackend.exceptions.ConflictException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshRExpiration;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));
    }

    @Transactional
    public AuthResult register(String username, String email, String password) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already in use: " + email);
        }
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));
        User user = new User(
                username,
                email,
                passwordEncoder.encode(password),
                Set.of(userRole)
        );
        userRepository.save(user);
        return buildAuthResult(user);
    }

    @Transactional
    public AuthResult login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }
        return buildAuthResult(user);
    }

    @Transactional
    public RefreshResult rotateRefreshToken(String oldToken){
        Optional<RefreshToken> stored = refreshTokenRepository.findByToken(oldToken);

        if (stored.isEmpty()) {
            // Token not in DB means it was already rotated — possible reuse attempt by an attacker, revoke all tokens for this user
            try {
                String email = jwtService.extractUsername(oldToken);
                userRepository.findByEmail(email)
                        .ifPresent(refreshTokenRepository::deleteByUser);
            } catch (Exception ignored) {}
            throw new BadCredentialsException("Invalid refresh token");
        }

        RefreshToken token = stored.get();
        if(token.getExpiresAt().isBefore(Instant.now())){
            refreshTokenRepository.delete(token);
            throw new BadCredentialsException("Refresh token expired");
        }
        User user = token.getUser();
        if(!jwtService.validateToken(oldToken, user)){
            refreshTokenRepository.delete(token);
            throw new BadCredentialsException("Invalid refresh token");
        }
        refreshTokenRepository.delete(token);
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        refreshTokenRepository.save(
                new RefreshToken(newRefreshToken, user, Instant.now().plusMillis(refreshRExpiration)));
        return new RefreshResult(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void revokeRefreshToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void revokeAllRefreshTokens(User user){
        refreshTokenRepository.deleteByUser(user);
    }

    private AuthResult buildAuthResult(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getAuthority)
                .toList();
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        // Eagerly prune expired tokens for this user to keep the refresh_tokens table from growing unboundedly
        refreshTokenRepository.deleteByUserAndExpiresAtBefore(user, Instant.now());
        refreshTokenRepository.save(new RefreshToken(
                refreshToken, user, Instant.now().plusMillis(refreshRExpiration)
        ));
        return new AuthResult(accessToken, refreshToken, user.getUsername(), user.getDisplayName(), roles);
    }
}
