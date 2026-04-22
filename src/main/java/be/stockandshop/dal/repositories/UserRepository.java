package be.stockandshop.dal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import be.stockandshop.dal.entities.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
