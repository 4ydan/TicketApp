package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.TicketToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import at.ac.tuwien.sepm.groupphase.backend.exception.AccountLockedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NegativeBonusPointException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.BookingService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingValidator bookingValidator;
    private final EmailService emailService;

    @PersistenceContext
    EntityManager entityManager;

    public BookingServiceImpl(PerformanceRepository performanceRepository, SeatRepository seatRepository,
                              TicketRepository ticketRepository, BookingRepository bookingRepository,
                              UserRepository userRepository, BookingValidator bookingValidator, EmailService emailService) {
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.bookingValidator = bookingValidator;
        this.emailService = emailService;
    }

    @Secured("ROLE_USER")
    @Override
    @Transactional
    public Long createBooking(BookingToCreateDto bookingToCreateDto) throws ValidationException, ConflictException {
        log.trace("Create new Booking {}", bookingToCreateDto);
        this.bookingValidator.validateNewBooking(bookingToCreateDto);
        ApplicationUser tempUser = this.userRepository.findById(bookingToCreateDto.getUserId()).get();
        if (!tempUser.getIsNotLocked()) {
            throw new AccountLockedException("Account " + tempUser.getEmail() + " is locked");
        }
        double totalPrice = 0;
        List<Ticket> tickets = new ArrayList<>();
        for (TicketToCreateDto ticketDto : bookingToCreateDto.getTickets()) {
            Performance tempPerformance = this.performanceRepository.findById(ticketDto.getPerformanceId()).get();
            Seat tempSeat;
            if (ticketDto.getIsStandingTicket()) {
                tempSeat = null;
            } else {
                tempSeat = this.seatRepository.findById(ticketDto.getSeat().getId()).get();
            }

            Ticket ticket = Ticket.builder()
                .performance(tempPerformance)
                .price(ticketDto.getPrice())
                .seat(tempSeat)
                .isStandingTicket(ticketDto.getIsStandingTicket())
                .build();
            tickets.add(ticket);
            totalPrice += ticketDto.getPrice();
        }

        int userBonusPoints = tempUser.getBonusPoints();
        userBonusPoints += (int) Math.round(totalPrice * 0.1);
        tempUser.setBonusPoints(userBonusPoints);
        this.entityManager.merge(tempUser);

        Booking booking = Booking.builder()
            .isPaid(bookingToCreateDto.getIsPaid())
            .tickets(tickets)
            .date(bookingToCreateDto.getDate())
            .isCanceled(Boolean.FALSE)
            .user(tempUser)
            .build();
        this.entityManager.persist(booking);

        long generatedBookingNum = booking.getId();
        Query query = this.entityManager.createNativeQuery("UPDATE TICKETS t SET t.BOOKING_ID = :bid WHERE t.id = :tid");
        query.setParameter("bid", generatedBookingNum);

        for (Ticket ticket : tickets) {
            query.setParameter("tid", ticket.getId());
            query.executeUpdate();
        }

        for (int i = 0; i < booking.getTickets().size(); i++) {
            if (booking.getTickets().get(i).isStandingTicket()) {
                int capacity = booking.getTickets().get(i).getPerformance().getHallPlan().getBookedNumOfStandingTickets();
                booking.getTickets().get(i).getPerformance().getHallPlan().setBookedNumOfStandingTickets(capacity + 1);
            } else {
                booking.getTickets().get(i).getSeat().setBooked(Boolean.TRUE);
            }
        }

        log.debug(booking.toString());
        return generatedBookingNum;
    }

    @Override
    public List<Booking> getSearchedBookings(BookingDto searchParameters) {
        log.trace("Search bookings ({})", searchParameters);
        ApplicationUser user = this.userRepository.getById(searchParameters.getUserId());
        return bookingRepository.findByUserAndIsPaidAndIsCanceled(user, searchParameters.getIsPaid(), searchParameters.getIsCanceled());
    }

    @Override
    @Transactional
    public void cancelBookings(List<BookingDto> bookingDtoList) throws NegativeBonusPointException {
        log.trace("Cancel bookings ({})", bookingDtoList);

        try {
            for (BookingDto tempBooking : bookingDtoList) {
                ApplicationUser user = this.userRepository.findById(tempBooking.getUserId()).get();
                int currentBonusPoints = user.getBonusPoints();
                double totalPrice = 0;
                List<Ticket> tempTicketList = tempBooking.getTickets();
                for (Ticket tempTicket : tempTicketList) {
                    totalPrice += tempTicket.getPrice();
                }
                currentBonusPoints -= (int) Math.round(totalPrice * 0.1);
                if (currentBonusPoints < 0) {
                    throw new NegativeBonusPointException("User cannot cancel tickets, as they have already spent the corresponding bonus points");
                }

                user.setBonusPoints(currentBonusPoints);

                this.entityManager.merge(user);
            }
        } catch (NoSuchElementException e) {
            log.debug("Provided user does not exist");
        }

        for (BookingDto bookingDto : bookingDtoList) {
            bookingRepository.updateBooking(bookingDto.getId());
        }
    }

    @Override
    @Transactional
    public void buyReservedBookings(List<BookingDto> bookingDtoList) {
        log.trace("Buy reserved bookings ({})", bookingDtoList);

        try {
            for (BookingDto tempBooking : bookingDtoList) {
                ApplicationUser user = this.userRepository.findById(tempBooking.getUserId()).get();
                int currentBonusPoints = user.getBonusPoints();
                double totalPrice = 0;
                List<Ticket> tempTicketList = tempBooking.getTickets();
                for (Ticket tempTicket : tempTicketList) {
                    totalPrice += tempTicket.getPrice();
                }
                currentBonusPoints += (int) Math.round(totalPrice * 0.1);
                user.setBonusPoints(currentBonusPoints);

                this.entityManager.merge(user);
            }
        } catch (NoSuchElementException e) {
            log.debug("Provided user does not exist");
        }

        for (BookingDto bookingDto : bookingDtoList) {
            bookingRepository.buyReservedBooking(bookingDto.getId());
        }
    }

}
