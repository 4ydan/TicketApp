package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDetailDto {
    private @NonNull Long id;
    private @NonNull String name;
    private @NonNull List<EventDto> events;
}
