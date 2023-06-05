package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SectorCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorCategory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SectorCategoryMapper {
    /**
     * Convert a list of sector category entities to a list of sector category dto {@link SectorCategoryDto}.
     *
     * @param  sectorCategoryList list of sector category entities to be converted
     * @return the converted {@link SectorCategoryDto}
     */
    List<SectorCategoryDto> entityToListDto(List<SectorCategory> sectorCategoryList);
}
