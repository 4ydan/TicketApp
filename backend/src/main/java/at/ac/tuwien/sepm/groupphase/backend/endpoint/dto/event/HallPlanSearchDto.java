package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class HallPlanSearchDto {
    private final @NotNull String term;
    private final int maxResults = 7;
}