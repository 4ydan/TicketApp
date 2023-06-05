package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.PictureFileManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class MerchandiseMapper {
    @Autowired
    private PictureFileManager pictureFileManager;

    /**
     * Convert a merchandise entity to a merchandise dto {@link MerchandiseDto}.
     *
     * @param merchandise the entity to be converted
     * @return the converted {@link MerchandiseDto}
     */
    @Mapping(source = "picture", target = "picture", qualifiedByName = "pictureToBytes")
    public abstract MerchandiseDto merchandiseToMerchandiseDto(Merchandise merchandise);

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
