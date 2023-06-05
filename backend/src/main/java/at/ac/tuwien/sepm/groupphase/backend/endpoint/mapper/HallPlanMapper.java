package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.HallPlanDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface HallPlanMapper {
    /**
     * Convert a list of hall-plan entities to a list of hall-plan dtos {@link HallPlanDto}.
     *
     * @param hallPlanList the list of artists to be converted
     * @return the converted {@link HallPlanDto}
     */
    List<HallPlanDto> entityToListDto(List<HallPlan> hallPlanList);
}
