package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.HallPlanSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SectorCategoryDto;

import java.util.List;

public interface HallPlanService {
    /**
     * Find all seats that belong to a hall plan.
     *
     * @param id the id of the hall plan
     * @return all seats of the plan.
     */
    List<SeatDto> getSeatingPlanByHallPlanId(long id);

    /**
     * Find all seats that belong to a hall plan.
     *
     * @param id the id of the hall plan
     * @return all seats of the plan.
     */
    List<SectorCategoryDto> getSectorCategoryByHallPlanId(Long id);

    /**
     * Find all hall-plan entries by the search criteria inside the searchDto.
     *
     * @param searchDto of the search parameters
     * @return ordered list of all fitting hall-plan entries
     */
    List<HallPlanDto> find(HallPlanSearchDto searchDto);
}
