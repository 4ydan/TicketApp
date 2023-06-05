package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NegativeBonusPointException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface BookingService {
    /**
     * Create a new booking.
     *
     * @param bookingToCreateDto the data of the new booking
     * @return the created booking number
     */
    Long createBooking(BookingToCreateDto bookingToCreateDto) throws ValidationException, ConflictException;

    /**
     * Get all bookings by search parameters.
     *
     * @param searchParameters search params
     * @return list of bookings that match params
     */
    List<Booking> getSearchedBookings(BookingDto searchParameters);

    /**
     * Cancel booking(s).
     *
     * @param bookingDtos list of the bookings to be canceled
     */
    void cancelBookings(List<BookingDto> bookingDtos) throws NegativeBonusPointException;

    /**
     * Buy a reserved booking(s).
     *
     * @param bookingDtos list of the bookings to be bought
     */
    void buyReservedBookings(List<BookingDto> bookingDtos);
}
