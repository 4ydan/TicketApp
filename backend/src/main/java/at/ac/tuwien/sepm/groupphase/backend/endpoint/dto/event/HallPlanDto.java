package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HallPlanDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Integer maxStandingCapacity;
    private final Integer bookedNumOfStandingTickets;
    private final Address address;
}
