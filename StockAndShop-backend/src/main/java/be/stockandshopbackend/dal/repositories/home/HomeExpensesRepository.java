package be.stockandshopbackend.dal.repositories.home;

import be.stockandshopbackend.dl.entities.home.HomeExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HomeExpensesRepository extends JpaRepository<HomeExpense, Long> {

    List<HomeExpense> findByHomeId(UUID homeId);
}
