package be.stockandshop.repositories;

import be.stockandshop.entities.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {

    @Query("SELECT uh.home FROM UserHome uh WHERE uh.user.id = :userId")
    List<Home> findAllByUserId(UUID userId);

}
