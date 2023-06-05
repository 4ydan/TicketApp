package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private Long addressId;
    private String country;
    private String city;
    private Long postalCode;
    private String street;
    private Long streetNr;

}
