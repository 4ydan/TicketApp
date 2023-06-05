package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.PictureFileManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public abstract class EventMapper {
    @Autowired
    private PictureFileManager pictureFileManager;

    /**
     * Convert a list of event entity to a list of event dto's {@link EventDto}.
     *
     * @param eventList the list of events to be converted
     * @return the converted {@link EventDto}
     */
    public abstract List<EventDto> entityToListDto(List<Event> eventList);

    /**
     * Convert an event entity to an event detail dto {@link EventDetailDto}.
     *
     * @param event the entity to be converted
     * @return the converted {@link EventDetailDto}
     */
    @Mapping(source = "image", target = "image", qualifiedByName = "pictureToBytes")
    public abstract EventDetailDto entityToDetailDto(Event event);

    /**
     * Convert an event entity to an event dto {@link EventDto}.
     *
     * @param event the entity to be converted
     * @return the converted {@link EventDto}
     */
    @Mapping(source = "image", target = "image", qualifiedByName = "pictureToBytes")
    public abstract EventDto entityToDto(Event event);

    /**
     * Convert an event entity to an event create/edit dto {@link EventCreateDetailDto}.
     *
     * @param event the entity to be converted
     * @return the converted {@link EventCreateDetailDto}
     */
    @Mapping(source = "image", target = "image", qualifiedByName = "pictureToBytes")
    @Mapping(source = "artist", target = "artist", qualifiedByName = "getArtistName")
    public abstract EventCreateDetailDto eventToEventCreateEditDetailDto(Event event);

    /**
     * Convert a picture saved in the persistent data store to a byte array.
     *
     * @param picture the picture to be converted
     * @return a byte array containing the requested picture
     */
    @Named("pictureToBytes")
    public byte[] pictureToBytes(Picture picture) {
        return pictureFileManager.getPictureByName(picture.getName());
    }

    /**
     * Get the name of an artist as string.
     *
     * @param artist the requested artist object
     * @return a string containing the name of the artist
     */
    @Named("getArtistName")
    public String getArtistName(Artist artist) {
        return artist.getName();
    }
}
