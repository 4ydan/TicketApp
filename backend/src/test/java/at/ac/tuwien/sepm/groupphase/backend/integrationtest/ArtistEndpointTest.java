package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.artist.ArtistDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ArtistEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private final Artist TEST_ARTIST = Artist.builder()
        .name("test-artist")
        .build();

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Event> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Event[].class));

        assertEquals(0, events.size());
    }

    @Test
    public void givenOneArtist_whenFindAll_thenListWithSizeOneAndArtistWithAllProperties()
        throws Exception {
        Artist expectedArtist = artistRepository.save(TEST_ARTIST);

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Artist> artists = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Artist[].class));

        assertEquals(1, artists.size());
        Artist artist = artists.get(0);
        assertAll(
            () -> assertEquals(expectedArtist.getId(), artist.getId()),
            () -> assertEquals(expectedArtist.getName(), artist.getName())
        );
    }

    @Test
    public void givenOneArtist_whenGetById_thenArtistWithAllPropertiesExceptPerformances() throws Exception {
        Artist expectedArtist = artistRepository.save(TEST_ARTIST);
        ArtistDetailDto expectedArtistDto = artistMapper.entityToDetailDto(expectedArtist);
        expectedArtistDto.setEvents(new ArrayList<>());

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/{id}", expectedArtist.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        ArtistDetailDto actualArtistDto = objectMapper.readValue(response.getContentAsString(),
            ArtistDetailDto.class);

        assertEquals(expectedArtistDto, actualArtistDto);
    }

    @Test
    public void givenNothing_whenGetById_thenNotFoundStatus() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI + "/{id}", -999L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void givenOneArtist_whenFindAllByTerm_thenNoArtistFound() throws Exception {
        artistRepository.save(TEST_ARTIST);

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI)
                .param("term", "asd")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Artist> artists = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Artist[].class));

        assertEquals(0, artists.size());
    }

    @Test
    public void givenTwoArtists_whenFindAllByTerm_thenTestArtistFound() throws Exception {
        Artist expectedArtist = artistRepository.save(TEST_ARTIST);
        artistRepository.save(TEST_ARTIST);

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI)
                .param("term", "test-artist")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Artist> artists = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Artist[].class));

        assertEquals(1, artists.size());
        Artist artist = artists.get(0);
        assertAll(
            () -> assertEquals(expectedArtist.getId(), artist.getId()),
            () -> assertEquals(expectedArtist.getName(), artist.getName())
        );
    }

    @Test
    public void givenTwoArtists_whenFindAllByShortTerm_thenTestArtistFound() throws Exception {
        Artist expectedArtist = artistRepository.save(TEST_ARTIST);
        artistRepository.save(TEST_ARTIST);

        MvcResult mvcResult = this.mockMvc.perform(get(ARTIST_BASE_URI)
                .param("term", "test-")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Artist> artists = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Artist[].class));

        assertEquals(1, artists.size());
        Artist artist = artists.get(0);
        assertAll(
            () -> assertEquals(expectedArtist.getId(), artist.getId()),
            () -> assertEquals(expectedArtist.getName(), artist.getName())
        );
    }
}
