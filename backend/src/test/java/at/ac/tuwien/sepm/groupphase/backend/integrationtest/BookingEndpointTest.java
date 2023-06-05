package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.BookingToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.TicketToCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Booking;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SectorCategory;
import at.ac.tuwien.sepm.groupphase.backend.repository.BookingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.HallPlanRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorCategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.SectorType;
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

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BookingEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private HallPlanRepository hallPlanRepository;

    @Autowired
    private SectorCategoryRepository sectorCategoryRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private final ApplicationUser TestApplicationUser =
        ApplicationUser.builder()
            .id(TEST_APPLICATION_USER_ID)
            .firstName(TEST_APPLICATION_USER_FIRST_NAME)
            .lastName(TEST_APPLICATION_USER_LAST_NAME)
            .email(TEST_APPLICATION_USER_EMAIL)
            .password(TEST_APPLICATION_USER_PASSWORD)
            .failedLogins(TEST_APPLICATION_USER_FAILED_LOGINS)
            .isActivated(TEST_APPLICATION_USER_IS_ACTIVATED)
            .isNotLocked(TEST_APPLICATION_USER_IS_NOT_LOCKED)
            .address(TEST_APPLICATION_USER_ADDRESS)
            .role(TEST_APPLICATION_USER_ADMIN)
            .bonusPoints(250)
            .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
            .build();

    Booking TEST_PAID_BOOKING = Booking.builder()
        .isPaid(true)
        .tickets(null)
        .date(LocalDate.of(2020, Month.JANUARY, 8))
        .isCanceled(false)
        .build();

    Booking TEST_CANCELED_BOOKING = Booking.builder()
        .isPaid(false)
        .tickets(null)
        .date(LocalDate.of(2020, Month.JANUARY, 8))
        .isCanceled(true)
        .build();

    @BeforeEach
    public void beforeEach() {
        confirmationTokenRepository.deleteAll();
        bookingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenFindAllPaidBookings_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(BOOKINGS_BASE_URI)
                .param("userId", TestApplicationUser.getId().toString())
                .param("isPaid", "true")
                .param("isCanceled", "false")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<BookingDto> bookingDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            BookingDto[].class));

        assertEquals(0, bookingDtos.size());
    }

    @Test
    public void givenNothing_whenFindAllCanceledBookings_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(BOOKINGS_BASE_URI)
                .param("userId", TestApplicationUser.getId().toString())
                .param("isPaid", "false")
                .param("isCanceled", "true")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<BookingDto> bookingDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            BookingDto[].class));

        assertEquals(0, bookingDtos.size());
    }

    @Test
    public void givenOnePaidBooking_whenFindAllCanceledBookings_thenNoBookingsFound() throws Exception {
        ApplicationUser user = userRepository.save(TestApplicationUser);
        TEST_CANCELED_BOOKING.setUser(user);
        bookingRepository.save(TEST_PAID_BOOKING);
        MvcResult mvcResult = this.mockMvc.perform(get(BOOKINGS_BASE_URI)
                .param("userId", user.getId().toString())
                .param("isPaid", "false")
                .param("isCanceled", "true")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<BookingDto> bookingDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            BookingDto[].class));

        assertEquals(0, bookingDtos.size());
    }

    @Test
    public void givenOneCanceledBooking_whenFindAllCanceledBookings_thenOneBookingFound() throws Exception {
        ApplicationUser user = userRepository.save(TestApplicationUser);
        TEST_CANCELED_BOOKING.setUser(user);
        bookingRepository.save(TEST_CANCELED_BOOKING);
        MvcResult mvcResult = this.mockMvc.perform(get(BOOKINGS_BASE_URI)
                .param("userId", user.getId().toString())
                .param("isPaid", "false")
                .param("isCanceled", "true")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<BookingDto> bookingDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            BookingDto[].class));

        assertEquals(1, bookingDtos.size());
    }

    @Test
    public void givenOneCanceledBooking_whenFindAllPaidBookings_thenNoBookingFound() throws Exception {
        ApplicationUser user = userRepository.save(TestApplicationUser);
        TEST_CANCELED_BOOKING.setUser(user);
        bookingRepository.save(TEST_CANCELED_BOOKING);
        MvcResult mvcResult = this.mockMvc.perform(get(BOOKINGS_BASE_URI)
                .param("userId", user.getId().toString())
                .param("isPaid", "true")
                .param("isCanceled", "false")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<BookingDto> bookingDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            BookingDto[].class));

        assertEquals(0, bookingDtos.size());
    }

    @Test
    public void givenOnePaidBooking_whenCreatePaidBookings_thenBookingCreated() throws Exception {
        // Persist sectory category in db
        SectorCategory sectorCategory = new SectorCategory();
        sectorCategory.setSectorType(SectorType.B);
        sectorCategory.setSurcharge(100.0);
        this.sectorCategoryRepository.save(sectorCategory);

        // Persist user and set user in booking
        ApplicationUser user = userRepository.save(TestApplicationUser);
        TEST_PAID_BOOKING.setUser(user);
        bookingRepository.save(TEST_PAID_BOOKING);
        hallPlanRepository.save(TEST_HALLPLAN);
        TEST_PERFORMANCE.setHallPlan(TEST_HALLPLAN);
        Performance performance = performanceRepository.save(TEST_PERFORMANCE);

        Seat seat = new Seat();
        seat.setSeatNumber(2);
        seat.setSeatRow(1);
        seat.setBooked(false);
        seat.setHallPlan(TEST_HALLPLAN);
        seat.setSectorCategory(sectorCategory);
        Seat seat1 = seatRepository.save(seat);

        SeatDto seatDto = SeatDto.builder()
            .id(seat1.getId())
            .isBooked(false)
            .seatRow(1)
            .seatNumber(2)
            .sectorType(SectorType.B)
            .build();

        BookingToCreateDto bookingToCreateDto = BookingToCreateDto.builder()
            .isPaid(true)
            .tickets(new TicketToCreateDto[]{new TicketToCreateDto(performance.getId(), 200.0, seatDto, false)})
            .date(LocalDate.of(2020, Month.JANUARY, 8))
            .userId(user.getId())
            .build();

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKINGS_BASE_URI)
                .content(objectMapper.writeValueAsString(bookingToCreateDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        List<Booking> booking = bookingRepository.findByUserAndIsPaidAndIsCanceled(user, TEST_PAID_BOOKING.getIsPaid(), TEST_PAID_BOOKING.getIsCanceled());
        Booking oneBooking = booking.get(0);
        assertEquals(oneBooking.getIsPaid(), TEST_PAID_BOOKING.getIsPaid());
        assertEquals(oneBooking.getDate(), TEST_PAID_BOOKING.getDate());
    }
}