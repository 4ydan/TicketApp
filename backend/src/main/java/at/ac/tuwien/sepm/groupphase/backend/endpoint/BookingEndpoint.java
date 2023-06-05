package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.BookingMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NegativeBonusPointException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/bookings")
public class BookingEndpoint {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingEndpoint(
        BookingService bookingService,
        BookingMapper bookingMapper
    ) {

        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Booking")
    public Long createBooking(@RequestBody BookingToCreateDto bookingToCreateDto) throws ValidationException, ConflictException {
        log.info("POST /api/v1/booking/{}", bookingToCreateDto);
        return this.bookingService.createBooking(bookingToCreateDto);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all bookings from User by search parameters")
    public List<BookingDto> getBookings(BookingDto searchParameters) {
        log.info("GET /api/v1/bookings");
        log.debug("request parameters: {}", searchParameters);
        return this.bookingService.getSearchedBookings(searchParameters)
            .stream().map(bookingMapper::entityToBookingDto).collect(Collectors.toList());
    }

    @Secured("ROLE_USER")
    @PostMapping("/canceled")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancel bookings")
    public void cancelBookings(@RequestBody List<BookingDto> bookingDtoList) {
        log.info("POST /api/v1/bookings/cancel");
        try {
            this.bookingService.cancelBookings(bookingDtoList);
        } catch (NegativeBonusPointException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The user does not have enough bonus points to cancel the booking.");
        }
    }

    @Secured("ROLE_USER")
    @PostMapping("/reserved")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Buy reserved bookings")
    public void buyReservedBookings(@RequestBody List<BookingDto> bookingDtoList) {
        log.info("POST /api/v1/bookings/reserved");
        log.debug("request body: {}", bookingDtoList);
        this.bookingService.buyReservedBookings(bookingDtoList);
    }
}
