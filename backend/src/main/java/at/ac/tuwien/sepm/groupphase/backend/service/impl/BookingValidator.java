package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.TicketToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorCategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.SectorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;


@Slf4j
@Component
public class BookingValidator {
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final SectorCategoryRepository sectorCategoryRepository;

    /**
     * Validation of user objects.
     *
     * @param performanceRepository    performance repository of performances
     * @param seatRepository           seat repository of seats
     * @param userRepository           user repository of user
     * @param sectorCategoryRepository sector category repository of hall plan
     */
    public BookingValidator(PerformanceRepository performanceRepository, SeatRepository seatRepository, UserRepository userRepository,
                            SectorCategoryRepository sectorCategoryRepository) {
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.sectorCategoryRepository = sectorCategoryRepository;
    }

    /**
     * Validation for new incomming booking that will be saved in the database.
     *
     * @param newBooking that should be valid
     * @throws ValidationException if validation error(s) occured
     * @throws ConflictException   if conflict(s) in relation to other entities occured
     */
    public void validateNewBooking(BookingToCreateDto newBooking) throws ValidationException, ConflictException {
        log.trace("Validating new booking : ({})", newBooking);
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictErrors = new ArrayList<>();
        Set<Long> performanceIdSet = new HashSet<>();

        if (newBooking.getIsPaid() == null) {
            validationErrors.add("Booking paid status is null");
        }

        if (newBooking.getUserId() == null) {
            validationErrors.add("User is not given");
        } else if (!this.userRepository.existsById(newBooking.getUserId())) {
            validationErrors.add("User is not given");
        }

        if (newBooking.getDate() == null) {
            validationErrors.add("Date is not given");
        }

        for (TicketToCreateDto newTicket : newBooking.getTickets()) {
            this.validateNewTicket(newTicket, validationErrors, conflictErrors);
            performanceIdSet.add(newTicket.getPerformanceId());
        }

        this.validateFreeCapacityinHallPlan(Arrays.asList(newBooking.getTickets()), performanceIdSet, conflictErrors);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of new booking is invalid", validationErrors);
        }

        if (!conflictErrors.isEmpty()) {
            throw new ConflictException("The data of to create new booking has conflict(s)", conflictErrors);
        }
    }

    /**
     * Validation for new incomming ticket that will be saved in the database.
     *
     * @param newTicket        that should be valid
     * @param validationErrors that should be saved temporary in
     * @param newTicket        that should be saved temporary in
     */
    private void validateNewTicket(TicketToCreateDto newTicket, List<String> validationErrors, List<String> conflictErrors) {
        log.trace("Validating new booking : ({})", newTicket);

        if (newTicket.getIsStandingTicket() == null) {
            validationErrors.add("Status of if the ticket is standing ticket is not given");
        }

        if (newTicket.getPrice() == null) {
            validationErrors.add("Price of the ticket is not given");
        }

        if (newTicket.getSeat() == null) {
            if (newTicket.getIsStandingTicket() == false) {
                conflictErrors.add("Ticket is not a standing ticket, but no seat given");
            }
        }

        if (newTicket.getSeat() != null) {
            if (newTicket.getIsStandingTicket() == true) {
                conflictErrors.add("Ticket is a standing ticket, but seat given");
            }
            if (this.seatRepository.existsById(newTicket.getSeat().getId())) {
                Seat seatInSystem = this.seatRepository.findById(newTicket.getSeat().getId()).get();
                if (newTicket.getSeat().getSeatNumber() != seatInSystem.getSeatNumber()) {
                    conflictErrors.add("The given seat number of the ticket is not correct");
                }
                if (newTicket.getSeat().getSeatRow() != seatInSystem.getSeatRow()) {
                    conflictErrors.add("The given seat row of the ticket is not correct");
                }
                if (newTicket.getSeat().getSectorType() != seatInSystem.getSectorCategory().getSectorType()) {
                    conflictErrors.add("The given sector type of the ticket is not correct");
                }
                if (seatInSystem.isBooked()) {
                    Optional<Performance> performanceInSystem = this.performanceRepository.findById(newTicket.getPerformanceId());
                    if (performanceInSystem.isPresent()) {
                        conflictErrors.add(
                            "The seat (row: " + newTicket.getSeat().getSeatRow() + " number: " + newTicket.getSeat().getSeatNumber() + " for performance: "
                                + performanceInSystem.get().getName() + ") is already booked");
                    } else {
                        conflictErrors.add(
                            "The seat (row: " + newTicket.getSeat().getSeatRow() + " number: " + newTicket.getSeat().getSeatNumber() + ") is already booked");
                    }
                }
            } else {
                validationErrors.add("Seat Id is not valid");
            }
        }

        if (newTicket.getPerformanceId() == null) {
            validationErrors.add("Price of the ticket is not given");
        } else if (!this.performanceRepository.existsById(newTicket.getPerformanceId())) {
            validationErrors.add("Performance of the ticket is not given");
        } else {
            Performance performanceInSystem = this.performanceRepository.findById(newTicket.getPerformanceId()).get();
            Double caculatedPrice = performanceInSystem.getPrice();
            if (newTicket.getSeat() == null) {
                caculatedPrice += this.sectorCategoryRepository.findSectorCategoryBySectorType(SectorType.STANDING).getSurcharge();
            } else {
                caculatedPrice += this.sectorCategoryRepository.findSectorCategoryBySectorType(newTicket.getSeat().getSectorType()).getSurcharge();
            }
            if (this.roundToTowDecimal(newTicket.getPrice()) != this.roundToTowDecimal(caculatedPrice)) {
                conflictErrors.add("Price of the ticket is not correct");
            }
        }
    }

    private void validateFreeCapacityinHallPlan(List<TicketToCreateDto> newTickets, Set<Long> performanceIdSet, List<String> conflictErrors) {
        log.trace("Validating free capacity of each hall plan");
        for (Long performanceId : performanceIdSet) {
            int count = 0;
            for (TicketToCreateDto newTicket : newTickets) {
                if (newTicket.getPerformanceId().longValue() == performanceId.longValue() && newTicket.getIsStandingTicket()) {
                    count++;
                }
            }
            Performance performanceInSystem = this.performanceRepository.findById(performanceId).get();
            int freeCapacityOfStandingArea =
                performanceInSystem.getHallPlan().getMaxStandingCapacity() - performanceInSystem.getHallPlan().getBookedNumOfStandingTickets();
            if (freeCapacityOfStandingArea < count) {
                conflictErrors.add("There is not enough capacity of standing places for performance: " + performanceInSystem.getHallPlan().getName());
            }
        }
    }

    private double roundToTowDecimal(double number) {
        return Math.round((number * 100.0) / 100.0);
    }
}
