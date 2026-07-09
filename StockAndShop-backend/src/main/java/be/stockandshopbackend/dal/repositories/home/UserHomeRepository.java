package be.stockandshopbackend.dal.repositories.home;

import be.stockandshopbackend.dl.entities.home.UserHome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserHomeRepository extends JpaRepository<UserHome, Long> {

    @Query("select uh from Home h join h.users uh where h.id = :homeId and uh.user.id = :userId")
    Optional<UserHome> findByHomeIdAndUserId(@Param("homeId") UUID homeId, @Param("userId") UUID userId);
}
