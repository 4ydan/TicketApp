package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.type.SectorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDto {
    private @NonNull Long id;
    private @NonNull Integer seatRow;
    private @NonNull Integer seatNumber;
    private @NonNull boolean isBooked;
    private @NonNull SectorType sectorType;
}
