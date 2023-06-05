package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ArtistListDto {
    private @NonNull Long id;
    private @NonNull String name;
}
