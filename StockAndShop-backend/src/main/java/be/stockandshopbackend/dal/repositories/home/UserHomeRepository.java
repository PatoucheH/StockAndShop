package be.stockandshopbackend.dal.repositories.home;

import be.stockandshopbackend.dl.entities.home.UserHome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHomeRepository extends JpaRepository<UserHome, Long> {
}
