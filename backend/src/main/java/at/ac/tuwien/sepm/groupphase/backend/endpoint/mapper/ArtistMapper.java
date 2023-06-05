package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistListDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ArtistMapper {
    /**
     * Convert a list of artist entity to a list of artist dto's {@link ArtistListDto}.
     *
     * @param artistList the list of artists to be converted
     * @return the converted {@link ArtistListDto}
     */
    List<ArtistListDto> entityToListDto(List<Artist> artistList);

    /**
     * Convert an artist entity to an artist detail dto {@link ArtistDetailDto}.
     *
     * @param artist the entity to be converted
     * @return the converted {@link ArtistDetailDto}
     */
    ArtistDetailDto entityToDetailDto(Artist artist);
}
