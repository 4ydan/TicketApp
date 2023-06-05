package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/artists")
public class ArtistEndpoint {
    private final ArtistService artistService;

    @Autowired
    public ArtistEndpoint(ArtistService artistService) {
        this.artistService = artistService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping
    @Operation(summary = "Get a list of all artists matching the search criteria", security = @SecurityRequirement(name = "apiKey"))
    public List<ArtistListDto> findAll(ArtistSearchDto searchDto) {
        log.info("GET /api/v1/artists");
        return artistService.find(searchDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PermitAll
    @GetMapping("{id}")
    @Operation(summary = "Get one artist with detailed info", security = @SecurityRequirement(name = "apiKey"))
    public ArtistDetailDto getById(@PathVariable long id) {
        log.info("GET /api/v1/artists/{}", id);
        return artistService.getById(id);
    }
}
