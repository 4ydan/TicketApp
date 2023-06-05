package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailedUserDto {

    @NonNull
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    @Email
    private String email;

    @NonNull
    private String password;

    @NonNull
    private short failedLogins;

    @NonNull
    private Boolean isActivated;

    private AddressDto address;

    @NonNull
    private UserRole role;

    private Long createFrom;
}

