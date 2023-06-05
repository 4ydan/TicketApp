package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Finds all the events by category and orders them by the start date.
     *
     * @param category the events category
     * @return all matching events.
     */
    @Query(
        "SELECT e FROM Event e"
            + " WHERE (:category is null OR e.eventCategory = :category)"
            + " GROUP BY e ORDER BY e.startDate"
    )
    List<Event> findAllByStartDateDesc(@Param("category") EventCategory category, Pageable pageable);

    /**
     * Finds all the events by additional performance filter parameters and orders them by the start date.
     *
     * @param duration    the duration of the event +- 30minutes
     * @param startTime   the startTime of the performance
     * @param endTime     the endTime of the performance
     * @param description the events description
     * @param category    the category of the event
     * @param startDate   the startDate of the event
     * @param endDate     the endDate of the event
     * @param street      the street of the performance
     * @param city        the city of the performance
     * @param country     the country of the performance
     * @param postalCode  the postalCode of the performance
     * @param minPrice    the min price of the performance
     * @param maxPrice    the max price of the performance
     * @return all matching events with the additional performance parameters.
     */
    @Query(
        "SELECT e FROM Event e LEFT JOIN Performance p ON p.event=e"
            + " WHERE (:duration is null OR (e.durationInMinutes <= :duration+30 AND e.durationInMinutes >= :duration-30))"
            + " AND (:startTime is null OR p.startTime >= :startTime)"
            + " AND (:endTime is null OR p.endTime <= :endTime)"
            + " AND (:description is null OR :description='' OR UPPER(e.description) LIKE UPPER(CONCAT( '%', :description, '%'))) "
            + " AND (:category is null OR e.eventCategory = :category)"
            + " AND (:startDate is null OR e.startDate >= :startDate)"
            + " AND (:endDate is null OR e.startDate <= :endDate)"
            + " AND (:street is null OR :street='' OR UPPER(p.address.street) LIKE UPPER(CONCAT( '%', :street, '%'))) "
            + " AND (:city is null OR p.address.city = :city)"
            + " AND (:country is null OR p.address.country = :country)"
            + " AND (:postalCode is null OR p.address.postalCode = :postalCode)"
            + " AND (:minPrice is null OR p.price >= :minPrice)"
            + " AND (:maxPrice is null OR p.price <= :maxPrice)"
            + " GROUP BY e ORDER BY e.startDate"
    )
    List<Event> findAllByCriteriaStartDateDesc(@Param("duration") Integer duration,
                                               @Param("startTime") LocalTime startTime,
                                               @Param("endTime") LocalTime endTime,
                                               @Param("description") String description,
                                               @Param("category") EventCategory category,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("street") String street,
                                               @Param("city") String city,
                                               @Param("country") String country,
                                               @Param("postalCode") Long postalCode,
                                               @Param("minPrice") Double minPrice,
                                               @Param("maxPrice") Double maxPrice,
                                               Pageable pageable
    );

    /**
     * Finds all the events by category and sorts them alphabetically descending.
     *
     * @param category the events category
     * @return all matching events.
     */
    @Query(
        "SELECT e FROM Event e"
            + " WHERE (:category is null OR e.eventCategory = :category)"
            + " GROUP BY e ORDER BY e.title"
    )
    List<Event> findAllByTitleDesc(
        @Param("category") EventCategory category,
        Pageable pageable
    );

    /**
     * Finds all the events by additional performance filter parameters and orders them alphabetically descending.
     *
     * @param duration    the duration of the event +- 30minutes
     * @param startTime   the startTime of the performance
     * @param endTime     the endTime of the performance
     * @param description the events description
     * @param category    the category of the event
     * @param startDate   the startDate of the event
     * @param endDate     the endDate of the event
     * @param street      the street of the performance
     * @param city        the city of the performance
     * @param country     the country of the performance
     * @param postalCode  the postalCode of the performance
     * @param minPrice    the min price of the performance
     * @param maxPrice    the max price of the performance
     * @return all matching events with the additional performance parameters.
     */
    @Query(
        "SELECT e FROM Event e LEFT JOIN Performance p ON p.event=e"
            + " WHERE (:duration is null OR (e.durationInMinutes <= :duration+30 AND e.durationInMinutes >= :duration-30))"
            + " AND (:startTime is null OR p.startTime >= :startTime)"
            + " AND (:endTime is null OR p.endTime <= :endTime)"
            + " AND (:description is null OR :description='' OR UPPER(e.description) LIKE UPPER(CONCAT( '%', :description, '%'))) "
            + " AND (:category is null OR e.eventCategory = :category)"
            + " AND (:startDate is null OR e.startDate >= :startDate)"
            + " AND (:endDate is null OR e.startDate <= :endDate)"
            + " AND (:street is null OR :street='' OR UPPER(p.address.street) LIKE UPPER(CONCAT( '%', :street, '%'))) "
            + " AND (:city is null OR p.address.city = :city)"
            + " AND (:country is null OR p.address.country = :country)"
            + " AND (:postalCode is null OR p.address.postalCode = :postalCode)"
            + " AND (:minPrice is null OR p.price >= :minPrice)"
            + " AND (:maxPrice is null OR p.price <= :maxPrice)"
            + " GROUP BY e ORDER BY e.title"
    )
    List<Event> findAllByCriteriaTitleDesc(@Param("duration") Integer duration,
                                           @Param("startTime") LocalTime startTime,
                                           @Param("endTime") LocalTime endTime,
                                           @Param("description") String description,
                                           @Param("category") EventCategory category,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("street") String street,
                                           @Param("city") String city,
                                           @Param("country") String country,
                                           @Param("postalCode") Long postalCode,
                                           @Param("minPrice") Double minPrice,
                                           @Param("maxPrice") Double maxPrice,
                                           Pageable pageable);

    /**
     * Finds all the events by artist id.
     *
     * @param id the artists id
     * @return all matching events.
     */
    List<Event> findByArtist_Id(Long id);

    /**
     * Finds all the events by title and ignore lower/upper case.
     *
     * @param title the events title
     * @return all matching events.
     */
    List<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);


    /**
     * Finds all the events by category with the amount of sold tickets.
     *
     * @param eventCategory the events category
     * @return all matching events with the number of sold tickets in descending order.
     */
    @Query("SELECT e, COUNT(t) FROM Event e JOIN Performance p ON p.event = e JOIN Ticket t ON t.performance = p"
        + " WHERE e.eventCategory = :category"
        + " AND p.date <= :toDate"
        + " AND p.date >= :fromDate"
        + " AND (:duration is null OR (e.durationInMinutes <= :duration+30 AND e.durationInMinutes >= :duration-30))"
        + " AND (:description is null OR :description='' OR UPPER(e.description) LIKE UPPER(CONCAT( '%', :description, '%'))) "
        + " GROUP BY p.event ORDER BY COUNT(t) DESC"
    )
    List<Object[]> findTopTenEventsByCategory(@NonNull @Param("fromDate") LocalDate fromDate,
                                              @NonNull @Param("toDate") LocalDate toDate,
                                              @NonNull @Param("category") EventCategory eventCategory,
                                              @Param("duration") Integer duration,
                                              @Param("description") String description,
                                              Pageable pageable);

}
