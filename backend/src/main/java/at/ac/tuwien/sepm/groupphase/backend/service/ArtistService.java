package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistSearchDto;

import java.util.List;

public interface ArtistService {
    /**
     * Find all artists entries by the search criteria inside the searchDto.
     *
     * @param searchDto the search parameters
     * @return ordered list of all artist entries.
     */
    List<ArtistListDto> find(ArtistSearchDto searchDto);

    /**
     * Get an artist by id.
     *
     * @param id the id of the artist
     * @return ordered list of all artist entries
     */
    ArtistDetailDto getById(long id);
}
