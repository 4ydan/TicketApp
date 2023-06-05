package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event;

import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDto {
    private Long id;
    private String name;
    private String country;
    private String city;
    private Long postalCode;
    private String street;
    private Long streetNr;
    private String date;
    private Double price;
    private String startTime;
    private String endTime;
    private HallPlan hallPlan;
}