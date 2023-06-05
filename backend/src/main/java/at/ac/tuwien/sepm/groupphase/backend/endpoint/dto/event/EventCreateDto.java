package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@Setter
public class EventCreateDto {
    private final Long id;
    private final @NonNull String title;
    private final MultipartFile image;
    private final @NonNull String description;
    private final @NonNull EventCategory eventCategory;
    private final @NonNull String startDate;
    private final @NonNull String endDate;
    private final @NonNull Integer durationInMinutes;
    private final @NonNull String artistName;
}
