package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.TicketToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandisePurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Purchase;
import at.ac.tuwien.sepm.groupphase.backend.exception.AccountLockedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.BookingService;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;

@Slf4j
@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final MerchandiseRepository merchandiseRepository;

    private final UserRepository userRepository;

    private final MerchandiseValidator merchandiseValidator;

    private final BookingService bookingService;

    @PersistenceContext
    EntityManager entityManager;

    public PurchaseServiceImpl(MerchandiseRepository merchandiseRepository, UserRepository userRepository, MerchandiseValidator merchandiseValidator,
                               BookingService bookingService) {
        this.merchandiseRepository = merchandiseRepository;
        this.userRepository = userRepository;
        this.merchandiseValidator = merchandiseValidator;
        this.bookingService = bookingService;
    }

    @Override
    @Transactional
    public void purchase(PurchaseDto purchaseDto) throws ValidationException, ConflictException {
        log.trace("Purchase {}", purchaseDto);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ApplicationUser user = userRepository.findUserByEmail(userEmail);
        if (!user.getIsNotLocked()) {
            throw new AccountLockedException("Account " + userEmail + " is locked");
        }
        merchandiseValidator.validateForPurchase(purchaseDto.getMerchandises());
        if (purchaseDto.getTickets() != null) {
            BookingToCreateDto booking =
                new BookingToCreateDto(true, purchaseDto.getTickets().toArray(TicketToCreateDto[]::new), LocalDate.now(), user.getId());
            bookingService.createBooking(booking);
        }

        double totalPrice = 0;
        for (MerchandisePurchaseDto merchandiseDto : purchaseDto.getMerchandises()) {
            Merchandise merchandise = merchandiseRepository.findById(merchandiseDto.getId()).get();
            Purchase purchase = Purchase.builder()
                .date(LocalDate.now())
                .amount(merchandiseDto.getAmount())
                .user(user)
                .merchandise(merchandise)
                .build();
            this.entityManager.persist(purchase);
            merchandise.setInStock(merchandise.getInStock() - merchandiseDto.getAmount());
            this.merchandiseRepository.save(merchandise);
            totalPrice += merchandise.getPrice() * merchandiseDto.getAmount();
        }

        int userBonusPoints = user.getBonusPoints();
        userBonusPoints += (int) Math.round(totalPrice * 0.1);
        user.setBonusPoints(userBonusPoints);
        this.entityManager.merge(user);

    }

    @Override
    @Transactional
    public void purchaseByVendorForCustomer(PurchaseDto purchaseDto, String email) throws ValidationException, ConflictException {
        log.debug("Purchase as vendor {} for {}", purchaseDto, email);
        ApplicationUser user = userRepository.findUserByEmail(email);
        if (!user.getIsNotLocked()) {
            throw new AccountLockedException("Account " + email + " is locked");
        }
        merchandiseValidator.validateForPurchase(purchaseDto.getMerchandises());
        if (purchaseDto.getTickets() != null) {
            BookingToCreateDto booking =
                new BookingToCreateDto(true, purchaseDto.getTickets().toArray(TicketToCreateDto[]::new), LocalDate.now(), user.getId());
            bookingService.createBooking(booking);
        }

        double totalPrice = 0;
        for (MerchandisePurchaseDto merchandiseDto : purchaseDto.getMerchandises()) {
            Merchandise merchandise = merchandiseRepository.findById(merchandiseDto.getId()).get();
            Purchase purchase = Purchase.builder()
                .date(LocalDate.now())
                .amount(merchandiseDto.getAmount())
                .user(user)
                .merchandise(merchandise)
                .build();
            this.entityManager.persist(purchase);
            merchandise.setInStock(merchandise.getInStock() - merchandiseDto.getAmount());
            this.merchandiseRepository.save(merchandise);
            totalPrice += merchandise.getPrice() * merchandiseDto.getAmount();
        }

        int userBonusPoints = user.getBonusPoints();
        userBonusPoints += (int) Math.round(totalPrice * 0.1);
        user.setBonusPoints(userBonusPoints);
        this.entityManager.merge(user);
    }
}
