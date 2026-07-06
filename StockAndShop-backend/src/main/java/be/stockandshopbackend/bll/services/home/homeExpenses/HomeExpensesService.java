package be.stockandshopbackend.bll.services.home.homeExpenses;

import be.stockandshopbackend.dl.entities.home.HomeExpense;

import java.util.List;
import java.util.UUID;

public interface HomeExpensesService {

    void createHomeExpense(HomeExpense homeExpense);
    List<HomeExpense> findByHomeId(UUID homeId);

}
