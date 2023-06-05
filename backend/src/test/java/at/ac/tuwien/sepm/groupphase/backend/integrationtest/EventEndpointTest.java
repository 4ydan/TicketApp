package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.type.EventSort;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;
    private final Picture picture1 = Picture.builder().name("test1.jpg").build();
    private final Picture picture2 = Picture.builder().name("test1.jpg").build();
    private final Picture picture4 = Picture.builder().name("test1.jpg").build();
    private final Picture picture5 = Picture.builder().name("test1.jpg").build();

    Event TEST_CINEMA_EVENT = Event.builder()
        .title("CinemaEvent")
        .image(picture1)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.CINEMA)
        .build();

    Event TEST_OPERA_EVENT = Event.builder()
        .title("OperaEvent")
        .image(picture2)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.OPERA)
        .build();

    Event TEST_CONCERT_EVENT = Event.builder()
        .title("ConcertEvent")
        .image(picture4)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.CONCERT)
        .build();

    Event TEST_THEATRE_EVENT = Event.builder()
        .title("TheatreEvent")
        .image(picture5)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2020, Month.JANUARY, 8))
        .durationInMinutes(60)
        .eventCategory(EventCategory.THEATRE)
        .build();


    @BeforeEach
    public void beforeEach() {
        performanceRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI)
                .param("sort", EventSort.DATE.toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));

        assertEquals(0, events.size());
    }

    @Test
    public void givenThreeEvent_whenFindAll_thenListWithSizeOneAndEventWithAllProperties()
        throws Exception {
        Event expectedEvent1 = eventRepository.save(TEST_CINEMA_EVENT);
        Event expectedEvent2 = eventRepository.save(TEST_CONCERT_EVENT);
        Event expectedEvent3 = eventRepository.save(TEST_OPERA_EVENT);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI)
                .param("page", "0")
                .param("perPage", "10")
                .param("sort", EventSort.DATE.toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        List<EventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));

        assertEquals(3, events.size());

        EventDto event1 = events.get(0);
        EventDto event2 = events.get(1);
        EventDto event3 = events.get(2);
        assertAll(
            () -> assertEquals(eventMapper.entityToDto(expectedEvent1), event1),
            () -> assertEquals(eventMapper.entityToDto(expectedEvent2), event2),
            () -> assertEquals(eventMapper.entityToDto(expectedEvent3), event3)
        );
    }

    @Test
    public void givenTwoEvents_whenGetById_thenEventWithAllPropertiesExceptPerformances() throws Exception {
        Event expectedEvent = eventRepository.save(TEST_CONCERT_EVENT);
        eventRepository.save(TEST_CINEMA_EVENT);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI + "/{id}", expectedEvent.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        EventDetailDto actualEventDto = objectMapper.readValue(response.getContentAsString(),
            EventDetailDto.class);

        EventDetailDto expectedEventDto = eventMapper.entityToDetailDto(expectedEvent);
        expectedEventDto.setPerformances(new ArrayList<>());

        assertEquals(expectedEventDto, actualEventDto);
    }

    @Test
    public void givenNothing_whenGetById_thenNotFoundStatus() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI + "/{id}", -999L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void givenOneEvent_whenFindAllByTerm_thenNoEventFound() throws Exception {
        eventRepository.save(TEST_THEATRE_EVENT);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI)
                .param("term", "asd")
                .param("perPage", "1")
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
    public void givenTwoEvents_whenFindAllByTerm_thenTestEventFound() throws Exception {
        Event expectedEvent = eventRepository.save(TEST_OPERA_EVENT);
        eventRepository.save(TEST_CONCERT_EVENT);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI)
                .param("term", "OperaEvent")
                .param("category", String.valueOf(EventCategory.ALL))
                .param("sort", String.valueOf(EventSort.DATE))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Event> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Event[].class));

        assertEquals(1, events.size());
        Event event = events.get(0);
        assertAll(
            () -> assertEquals(expectedEvent.getId(), event.getId()),
            () -> assertEquals(expectedEvent.getTitle(), event.getTitle()),
            () -> assertEquals(expectedEvent.getDescription(), event.getDescription()),
            () -> assertEquals(expectedEvent.getEventCategory(), event.getEventCategory())
        );
    }

    @Test
    public void givenTwoEvents_whenFindAllByShortTerm_thenTestEventFound() throws Exception {
        Event expectedEvent = eventRepository.save(TEST_THEATRE_EVENT);
        eventRepository.save(TEST_OPERA_EVENT);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI)
                .param("term", "Thea")
                .param("perPage", "2")
                .param("category", String.valueOf(EventCategory.ALL))
                .param("sort", String.valueOf(EventSort.DATE))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EventDto> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));

        assertEquals(1, events.size());
        EventDto event = events.get(0);
        EventDto expectedEventDto = eventMapper.entityToDto(expectedEvent);
        assertAll(
            () -> assertEquals(expectedEventDto.getId(), event.getId()),
            () -> assertEquals(expectedEventDto.getTitle(), event.getTitle()),
            () -> assertEquals(expectedEventDto.getDescription(), event.getDescription()),
            () -> assertEquals(expectedEventDto.getEventCategory(), event.getEventCategory())
        );
    }

    @Test
    public void givenTwoEvents_whenFindAllByCategory_thenTestEventFound() throws Exception {
        Event expectedEvent = eventRepository.save(TEST_CINEMA_EVENT);
        eventRepository.save(TEST_OPERA_EVENT);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTS_BASE_URI)
                .param("page", "0")
                .param("perPage", "2")
                .param("category", String.valueOf(EventCategory.CINEMA))
                .param("sort", String.valueOf(EventSort.DATE))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Event> events = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Event[].class));

        assertEquals(1, events.size());
        Event event = events.get(0);
        assertAll(
            () -> assertEquals(expectedEvent.getId(), event.getId()),
            () -> assertEquals(expectedEvent.getTitle(), event.getTitle()),
            () -> assertEquals(expectedEvent.getDescription(), event.getDescription()),
            () -> assertEquals(expectedEvent.getEventCategory(), event.getEventCategory())
        );
    }

    @Test
    public void creatingNewEvent_returns200() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.multipart(EVENTS_BASE_URI)
                .file(TEST_EVENT)
                .param("title", "TestEventTitle")
                .param("description", "TestEventDescription")
                .param("eventCategory", "ALL")
                .param("startDate", "2023-05-13")
                .param("endDate", "2023-05-20")
                .param("durationInMinutes", "10")
                .param("artist", "TestEventArtist")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertThat(response.getContentAsString().contains("TestEventTitle")).isTrue();
        assertThat(response.getContentAsString().contains("TestEventDescription")).isTrue();
        assertThat(response.getContentAsString().contains("2023-05-13")).isTrue();
        assertThat(response.getContentAsString().contains("2023-05-20")).isTrue();
        assertThat(response.getContentAsString().contains("10")).isTrue();
        assertThat(response.getContentAsString().contains("TestEventArtist")).isTrue();
    }

    @Test
    public void creatingNewEventWithEmptyStrings_returns422() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.multipart(EVENTS_BASE_URI)
                .file(TEST_EVENT)
                .param("title", "")
                .param("description", "TestEventDescription")
                .param("eventCategory", "ALL")
                .param("startDate", "2023-05-13")
                .param("endDate", "2023-05-20")
                .param("durationInMinutes", "10")
                .param("artist", "TestEventArtist")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }
}
