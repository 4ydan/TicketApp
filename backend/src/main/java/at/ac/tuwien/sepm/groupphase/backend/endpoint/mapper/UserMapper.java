package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSessionDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    /**
     * Convert a detailed user dto into an application user entity {@link ApplicationUser}.
     *
     * @param dto to be converted
     * @return the converted {@link ApplicationUser}
     */
    @Mapping(target = "address", source = "dto.address")
    ApplicationUser detailedUserDtoToApplicationUser(DetailedUserDto dto);

    /**
     * Convert an application user entity to a detailed user dto {@link DetailedUserDto}.
     *
     * @param user the entity to be converted
     * @return the converted {@link DetailedUserDto}
     */
    @Mapping(target = "address.addressId", source = "address.id")
    @Mapping(target = "address.country", source = "address.country")
    @Mapping(target = "address.city", source = "address.city")
    @Mapping(target = "address.postalCode", source = "address.postalCode")
    @Mapping(target = "address.street", source = "address.street")
    @Mapping(target = "address.streetNr", source = "address.streetNr")
    DetailedUserDto applicationUserToDetailedUserDto(ApplicationUser user);

    /**
     * Convert an application user entity to a user session dto {@link UserSessionDto}.
     *
     * @param user the entity to be converted
     * @return the converted {@link UserSessionDto}
     */
    @Mapping(target = "address.addressId", source = "address.id")
    @Mapping(target = "address.country", source = "address.country")
    @Mapping(target = "address.city", source = "address.city")
    @Mapping(target = "address.postalCode", source = "address.postalCode")
    @Mapping(target = "address.street", source = "address.street")
    @Mapping(target = "address.streetNr", source = "address.streetNr")
    UserSessionDto applicationUserToUserSessionDto(ApplicationUser user);

    /**
     * Convert a user session dto to an application user entity {@link ApplicationUser}.
     *
     * @param user dto to be converted
     * @return the converted {@link ApplicationUser}
     */
    @Mapping(source = "address.addressId", target = "address.id")
    @Mapping(source = "address.country", target = "address.country")
    @Mapping(source = "address.city", target = "address.city")
    @Mapping(source = "address.postalCode", target = "address.postalCode")
    @Mapping(source = "address.street", target = "address.street")
    @Mapping(source = "address.streetNr", target = "address.streetNr")
    ApplicationUser userSessionDtoToApplicationUser(UserSessionDto user);

}
