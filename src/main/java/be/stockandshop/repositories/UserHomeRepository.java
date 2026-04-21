package be.stockandshop.repositories;

import be.stockandshop.entities.UserHome;
import be.stockandshop.enums.HomeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserHomeRepository extends JpaRepository<UserHome, Long> {

    boolean existsByUserIdAndHomeIdAndRole(UUID userId, Long homeId, HomeRole role);
}
