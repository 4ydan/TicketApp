package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PerformanceEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PerformanceMapper performanceMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private HallPlanRepository hallPlanRepository;

    @BeforeEach
    public void beforeEach() {
        performanceRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    public void givenOnePerformance_whenGetById_thenEventWithAllPropertiesExceptPerformances() throws Exception {
        Performance performance = performanceRepository.save(TEST_PERFORMANCE);

        MvcResult mvcResult = this.mockMvc.perform(get(PERFORMANCE_BASE_URI + "/{id}", performance.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        PerformanceDto actualDto = objectMapper.readValue(response.getContentAsString(),
            PerformanceDto.class);

        PerformanceDto expectedDto = performanceMapper.entityToDto(performance);

        assertAll(
            () -> assertEquals(expectedDto.getId(), actualDto.getId()),
            () -> assertEquals(expectedDto.getName(), actualDto.getName()),
            () -> assertEquals(expectedDto.getCountry(), actualDto.getCountry()),
            () -> assertEquals(expectedDto.getCity(), actualDto.getCity()),
            () -> assertEquals(expectedDto.getPostalCode(), actualDto.getPostalCode()),
            () -> assertEquals(expectedDto.getStreet(), actualDto.getStreet()),
            () -> assertEquals(expectedDto.getStreetNr(), actualDto.getStreetNr()),
            () -> assertEquals(expectedDto.getDate(), actualDto.getDate()),
            () -> assertEquals(expectedDto.getPrice(), actualDto.getPrice()),
            () -> assertEquals(expectedDto.getStartTime(), actualDto.getStartTime()),
            () -> assertEquals(expectedDto.getEndTime(), actualDto.getEndTime())
        );
    }

    @Test
    public void givenNothing_whenGetById_thenNotFoundStatus() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(PERFORMANCE_BASE_URI + "/{id}", -999L)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void creatingNewPerformance_returns200() throws Exception {
        Event event = eventRepository.save(TEST_CINEMA_EVENT);
        HallPlan hallPlan = hallPlanRepository.save(TEST_HALLPLAN);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.multipart(PERFORMANCE_BASE_URI)
                .param("eventId", event.getId().toString())
                .param("name", "TestPerformanceName")
                .param("country", "TestCountry")
                .param("city", "TestCity")
                .param("postalCode", "1234")
                .param("street", "TestStreet")
                .param("streetNr", "1234")
                .param("date", "2023-05-15")
                .param("startTime", "18:00:00")
                .param("price", "100.0")
                .param("hallPlanId", hallPlan.getId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertThat(response.getContentAsString().contains("TestPerformanceName")).isTrue();
        assertThat(response.getContentAsString().contains("TestCountry")).isTrue();
        assertThat(response.getContentAsString().contains("1234")).isTrue();
        assertThat(response.getContentAsString().contains("TestCity")).isTrue();
        assertThat(response.getContentAsString().contains("TestCountry")).isTrue();
        assertThat(response.getContentAsString().contains("2023-05-15")).isTrue();
        assertThat(response.getContentAsString().contains("18:00:00")).isTrue();
        assertThat(response.getContentAsString().contains("100.0")).isTrue();
    }

    @Test
    public void creatingNewPerformanceWithEmptyStrings_returns422() throws Exception {
        Event event = eventRepository.save(TEST_CINEMA_EVENT);
        HallPlan hallPlan = hallPlanRepository.save(TEST_HALLPLAN);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.multipart(PERFORMANCE_BASE_URI)
                .param("eventId", event.getId().toString())
                .param("name", "")
                .param("country", "TestCountry")
                .param("city", "TestCity")
                .param("postalCode", "1234")
                .param("street", "TestStreet")
                .param("streetNr", "1234")
                .param("date", "2023-05-15")
                .param("startTime", "18:00")
                .param("price", "100.00")
                .param("hallPlanId", hallPlan.getId().toString())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

}
