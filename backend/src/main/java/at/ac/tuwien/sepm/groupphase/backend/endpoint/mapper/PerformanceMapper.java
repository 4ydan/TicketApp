package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface PerformanceMapper {
    /**
     * Convert a list of performance entities to a list of performance dto {@link PerformanceDto}.
     *
     * @param performance the list of performance entities to be converted
     * @return the converted {@link PerformanceDto}
     */
    List<PerformanceDto> entityToListDto(List<Performance> performance);

    /**
     * Convert a performance entity to a performance dto {@link PerformanceDto}.
     *
     * @param performance the list of performance entities to be converted
     * @return the converted {@link PerformanceDto}
     */
    @Mapping(target = "country", source = "address.country")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "postalCode", source = "address.postalCode")
    @Mapping(target = "streetNr", source = "address.streetNr")
    PerformanceDto entityToDto(Performance performance);
}
