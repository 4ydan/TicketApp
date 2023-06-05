package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ArtistServiceImpl implements ArtistService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    public ArtistServiceImpl(ArtistRepository artistRepository,
                             ArtistMapper artistMapper,
                             EventRepository eventRepository,
                             EventMapper eventMapper) {
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
        this.artistMapper = artistMapper;
        this.eventMapper = eventMapper;

    }

    @Override
    public List<ArtistListDto> find(ArtistSearchDto searchDto) {
        if (searchDto.getTerm() != null) {
            if (!searchDto.getTerm().isEmpty()) {
                log.trace("Find all artists by term {}", searchDto.getTerm());
                return this.artistMapper.entityToListDto(
                    this.artistRepository.findByNameContainingIgnoreCaseOrderByNameAsc(
                        searchDto.getTerm(),
                        PageRequest.of(0, searchDto.getMaxResults())
                    )
                );
            }
        }
        return artistMapper.entityToListDto(artistRepository.findAll());
    }

    @Override
    public ArtistDetailDto getById(long id) {
        log.trace("Get artist by id {}", id);
        Optional<Artist> artist = this.artistRepository.findById(id);
        if (artist.isEmpty()) {
            throw new NotFoundException("Artist not found");
        }
        ArtistDetailDto artistDetailDto = this.artistMapper.entityToDetailDto(artist.get());
        artistDetailDto.setEvents(eventMapper.entityToListDto(this.eventRepository.findByArtist_Id(id)));
        return artistDetailDto;
    }

    public Artist saveByNameIfNotExists(String name) {
        log.trace("Save artist by name {} if they do not exist already", name);
        Artist artist;
        Optional<Artist> dbArtist = this.artistRepository.findByNameIgnoreCase(name);

        if (dbArtist.isPresent()) {
            return dbArtist.get();
        } else {
            artist = Artist.builder().name(name).build();
            this.artistRepository.save(artist);
            return artist;
        }
    }
}
