package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailDto {
    private @NonNull Long id;
    private @NonNull String title;
    private @NonNull byte[] image;
    private @NonNull String description;
    private List<PerformanceDto> performances;
}
