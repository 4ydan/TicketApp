package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Get all user bookings that are previously bought and then cancelled.
     *
     * @param user       that cancelled booking
     * @param isPaid     denotes bought/reserved ticket
     * @param isCanceled denotes cancellation status
     * @return list of bought bookings that are cancelled
     */
    List<Booking> findByUserAndIsPaidAndIsCanceled(ApplicationUser user, boolean isPaid, boolean isCanceled);

    /**
     * Update booking table - add cancelled bookings.
     *
     * @param id of the booking to be cancelled
     */
    @Modifying
    @Query(value = "UPDATE Bookings  SET IS_CANCELED = 'true' WHERE id = :id", nativeQuery = true)
    void updateBooking(@Param("id") Long id);

    /**
     * Buy reserved booking.
     *
     * @param id of the reserved booking to be bought
     */
    @Modifying
    @Query(value = "UPDATE Bookings SET IS_PAID = 'true' WHERE id = :id", nativeQuery = true)
    void buyReservedBooking(@Param("id") Long id);

}