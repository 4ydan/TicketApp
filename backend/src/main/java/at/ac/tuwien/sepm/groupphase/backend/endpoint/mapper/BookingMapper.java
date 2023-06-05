package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookingMapper {

    /**
     * Convert a list of booking entities to a list of booking dto's {@link BookingDto}.
     *
     * @param booking the list of artists to be converted
     * @return the converted {@link BookingDto} as a List
     */
    @Mapping(target = "userId", source = "user.id")
    BookingDto entityToBookingDto(Booking booking);

}