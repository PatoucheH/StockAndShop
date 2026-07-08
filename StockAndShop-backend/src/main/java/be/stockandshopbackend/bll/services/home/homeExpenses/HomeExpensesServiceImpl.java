package be.stockandshopbackend.bll.services.home.homeExpenses;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.home.HomeExpensesRepository;
import be.stockandshopbackend.dal.repositories.home.UserHomeRepository;
import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.home.HomeExpense;
import be.stockandshopbackend.dl.entities.home.UserHome;
import be.stockandshopbackend.exceptions.ConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HomeExpensesServiceImpl extends BaseCRUDService<HomeExpense, Long, HomeExpensesRepository>
                                implements HomeExpensesService {

    private final UserHomeRepository userHomeRepository;

    public HomeExpensesServiceImpl(
            HomeExpensesRepository homeExpensesRepository,
            UserHomeRepository userHomeRepository
    ) {
        super(homeExpensesRepository);
        this.userHomeRepository = userHomeRepository;
    }

    @Transactional
    public void createHomeExpense(HomeExpense homeExpense) {
        List<UserHome> users = homeExpense.getUsersConcerned();
        UserHome payer = homeExpense.getPayer();
        if (users.isEmpty()) {
            throw new ConflictException("A home expense must concern at least one other user");
        }
        if (users.stream().anyMatch(u -> u.getId().equals(payer.getId()))) {
            throw new ConflictException("The payer cannot also be a concerned user");
        }
        Home home = homeExpense.getHome();
        Set<Long> homeMemberIds = home.getUsers().stream()
                .map(UserHome::getId)
                .collect(Collectors.toSet());
        boolean allBelongsToHome = homeMemberIds.contains(payer.getId())
                && users.stream().allMatch(u -> homeMemberIds.contains(u.getId()));
        if (!allBelongsToHome) {
            throw new ConflictException("Payer and concerned users must belong to the expense's home");
        }
        int priceByUser = homeExpense.getAmount() / (users.size() + 1);
        int payerCredit = priceByUser * users.size();
        payer.changeBalance(payerCredit);
        for (UserHome user : users) {
            user.changeBalance(-priceByUser);
        }
        userHomeRepository.save(payer);
        userHomeRepository.saveAll(users);
        repository.save(homeExpense);
    }

    public Page<HomeExpense> findByHomeId(UUID homeId, Pageable pageable) {
        return repository.findByHomeIdOrderByCreatedAtDesc(homeId, pageable);
    }
}
