package be.stockandshopbackend.bll.services.home.homeExpenses;

import be.stockandshopbackend.dl.entities.home.HomeExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HomeExpensesService {

    void createHomeExpense(HomeExpense homeExpense);
    Page<HomeExpense> findByHomeId(UUID homeId, Pageable pageable);

}
