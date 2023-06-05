package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper {

    /**
     * Convert an address dto to address entity {@link Address}.
     *
     * @param addressDto the address to be converted
     * @return the converted {@link Address}
     */
    Address addressDtoToAddress(AddressDto addressDto);

}
