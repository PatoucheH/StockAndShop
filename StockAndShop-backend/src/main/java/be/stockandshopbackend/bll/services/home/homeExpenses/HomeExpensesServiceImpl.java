package be.stockandshopbackend.bll.services.home.homeExpenses;

import be.stockandshopbackend.bll.services.base.BaseCRUDService;
import be.stockandshopbackend.dal.repositories.home.HomeExpensesRepository;
import be.stockandshopbackend.dal.repositories.home.HomeRepository;
import be.stockandshopbackend.dal.repositories.home.UserHomeRepository;
import be.stockandshopbackend.dl.entities.home.Home;
import be.stockandshopbackend.dl.entities.home.HomeExpense;
import be.stockandshopbackend.dl.entities.home.UserHome;
import be.stockandshopbackend.dl.enums.ExpenseType;
import be.stockandshopbackend.exceptions.ConflictException;
import be.stockandshopbackend.exceptions.NotFoundException;
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
    private final HomeRepository homeRepository;

    public HomeExpensesServiceImpl(
            HomeExpensesRepository homeExpensesRepository,
            UserHomeRepository userHomeRepository,
            HomeRepository homeRepository
    ) {
        super(homeExpensesRepository);
        this.userHomeRepository = userHomeRepository;
        this.homeRepository = homeRepository;
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
        // Integer division in cents: the remainder (at most totalParticipants - 1 cents)
        // is spread one cent at a time over the first concerned users so no cent is lost
        // and the sum of all balance changes stays exactly zero.
        int totalParticipants = users.size() + 1;
        int priceByUser = homeExpense.getAmount() / totalParticipants;
        int remainder = homeExpense.getAmount() % totalParticipants;
        int payerCredit = priceByUser * users.size() + remainder;
        payer.changeBalance(payerCredit);
        for (int i = 0; i < users.size(); i++) {
            int share = priceByUser + (i < remainder ? 1 : 0);
            users.get(i).changeBalance(-share);
        }
        userHomeRepository.save(payer);
        userHomeRepository.saveAll(users);
        repository.save(homeExpense);
    }

    public Page<HomeExpense> findByHomeId(UUID homeId, Pageable pageable) {
        return repository.findByHomeIdOrderByCreatedAtDesc(homeId, pageable);
    }

    @Transactional
    public void refundUser(UUID homeId, UUID payerUserId, UUID receiverUserId, int amount) {
        UserHome payer = userHomeRepository.findByHomeIdAndUserId(homeId, payerUserId).orElseThrow(
                () ->  new NotFoundException("Payer not found")
        );
        UserHome receiver = userHomeRepository.findByHomeIdAndUserId(homeId, receiverUserId).orElseThrow(
                () ->  new NotFoundException("Receiver not found")
        );
        payer.changeBalance(amount);
        receiver.changeBalance(-amount);
        userHomeRepository.save(payer);
        userHomeRepository.save(receiver);

        // Record the reimbursement as a REFUND line so it shows up in the shared history
        Home home = homeRepository.findById(homeId).orElseThrow(
                () -> new NotFoundException("Home not found")
        );
        HomeExpense refund = new HomeExpense(
                "Remboursement", amount, home, payer, List.of(receiver)
        );
        refund.setType(ExpenseType.REFUND);
        repository.save(refund);
    }
}
