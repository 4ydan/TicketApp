package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats that belong to a hall plan.
     *
     * @param id the id of the hall plan
     * @return all seats of the plan.
     */
    @Query(
        value = "SELECT new at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SeatDto(s.id, s.seatRow, s.seatNumber, s.isBooked, s.sectorCategory.sectorType)"
            + " FROM Seat s WHERE s.hallPlan.id = ?1"
    )
    List<SeatDto> findSeatsByHallPlanId(Long id);

    /**
     * Find all sector categories that belong to a hall plan.
     *
     * @param id the id of the hall plan
     * @return all sector categories of the plan.
     */
    @Query(
        value = "SELECT DISTINCT new SectorCategory(s.sectorCategory.id, s.sectorCategory.sectorType, s.sectorCategory.surcharge)"
            + " FROM Seat s WHERE s.hallPlan.id = ?1"
    )
    List<SectorCategory> findSectorCategoryByHallPlanId(Long id);
}
