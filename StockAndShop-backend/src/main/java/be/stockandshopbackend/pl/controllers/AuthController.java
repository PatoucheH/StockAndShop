package be.stockandshopbackend.pl.controllers;

import be.stockandshopbackend.bll.services.security.AuthResult;
import be.stockandshopbackend.bll.services.security.AuthService;
import be.stockandshopbackend.pl.DTOs.Response.AuthResponse;
import be.stockandshopbackend.pl.DTOs.requests.LoginRequest;
import be.stockandshopbackend.pl.DTOs.requests.RegisterRequest;
import be.stockandshopbackend.bll.services.security.RefreshResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.cookie-secure}")
    private boolean cookieSecure;

    @Value("${jwt.cookie-samesite}")
    private String cookieSamesite;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody @Valid RegisterRequest request,
                                                     HttpServletResponse response) {
        AuthResult result = authService.register(request);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(toAuthResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody @Valid LoginRequest request,
                                                  HttpServletResponse response) {
        AuthResult result = authService.login(request);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(toAuthResponse(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if(refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        RefreshResult result = authService.rotateRefreshToken(refreshToken);
        setRefreshTokenCookie(response, result.refreshToken());
        return ResponseEntity.ok(Map.of("accessToken", result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response){
        if (refreshToken != null) authService.revokeRefreshToken(refreshToken);
        clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSamesite)
                // Scoped to /api/auth so the browser only sends the refresh token on auth endpoints, not on every API call
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    private void clearRefreshTokenCookie(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSamesite)
                .path("/api/auth")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private AuthResponse toAuthResponse(AuthResult result){
        return new AuthResponse(result.accessToken(), result.email(), result.displayName(), result.roles());
    }
}
