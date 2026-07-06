package be.stockandshopbackend.dal.repositories.user;

import be.stockandshopbackend.dl.entities.user.RefreshToken;
import be.stockandshopbackend.dl.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUser(User user);
    void deleteByUserAndExpiresAtBefore(User user, Instant now);
}
