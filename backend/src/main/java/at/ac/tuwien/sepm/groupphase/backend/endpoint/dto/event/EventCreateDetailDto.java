package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import java.time.LocalDate;

@Data
@Builder
@Setter
public class EventCreateDetailDto {
    private final @NonNull Long id;
    private final @NonNull String title;
    private final @NonNull byte[] image;
    private final @NonNull String description;
    private final @NonNull EventCategory eventCategory;
    private final @NonNull LocalDate startDate;
    private final @NonNull LocalDate endDate;
    private final @NonNull Integer durationInMinutes;
    private final @NonNull String artist;
}
