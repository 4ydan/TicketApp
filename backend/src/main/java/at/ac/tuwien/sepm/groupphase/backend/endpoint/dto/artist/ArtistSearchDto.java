package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ArtistSearchDto {
    private final @NotNull String term;
    private final int maxResults = 7;
}
