package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.PictureFileManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class MessageMapper {

    @Autowired
    private PictureFileManager pictureFileManager;

    /**
     * Convert a message entity to a message dto {@link MessageDto}.
     *
     * @param message the entity to be converted
     * @return the converted {@link MessageDto}
     */
    @Mapping(source = "picture", target = "picture", qualifiedByName = "pictureToBytes")
    public abstract MessageDto messageToMessageDto(Message message);

    /**
     * Convert a message entity to a detailed message dto {@link DetailedMessageDto}.
     *
     * @param message the entity to be converted
     * @return the converted {@link DetailedMessageDto}
     */
    @Mapping(source = "picture", target = "picture", qualifiedByName = "pictureToBytes")
    public abstract DetailedMessageDto messageToDetailedMessageDto(Message message);

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

}