package be.stockandshopbackend.dal.repositories.home;

import be.stockandshopbackend.dl.entities.home.HomeExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HomeExpensesRepository extends JpaRepository<HomeExpense, Long> {

    Page<HomeExpense> findByHomeIdOrderByCreatedAtDesc(UUID homeId, Pageable pageable);
}
