package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.HallPlan;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.type.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.type.UserRole;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SHORT_DESCRIPTION = "Short Description Test";
    String TEST_NEWS_DESCRIPTION = "Description Test";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);
    String TEST_NEWS_PICTURE_NAME = "test1.jpg";

    String TEST_MERCHANDISE_NAME = "Merchandise";

    Double TEST_MERCHANDISE_PRICE = (double) 10;

    Integer TEST_MERCHANDISE_IN_STOCK = 100;

    Integer TEST_MERCHANDISE_VALID_AMOUNT = 1;

    Integer TEST_MERCHANDISE_INVALID_AMOUNT = 1000;

    Long TEST_MERCHANDISE_INVALID_ID = 2L;

    Integer TEST_MERCHANDISE_COSTS_POINTS = 50;

    String TEST_ADDRESS_COUNTRY = "Country";
    String TEST_ADDRESS_CITY = "City";
    Long TEST_ADDRESS_POSTAL_CODE = 2L;
    String TEST_ADDRESS_STREET = "Street";
    Long TEST_ADDRESS_STREETNR = 3L;

    Long TEST_APPLICATION_USER_ID = 4L;
    Long TEST_APPLICATION_USER_INVALID_ID = 5L;
    String TEST_APPLICATION_USER_FIRST_NAME = "FirstName";
    String TEST_APPLICATION_USER_LAST_NAME = "LastName";
    String TEST_APPLICATION_USER_EMAIL = "email@domain.com";
    String TEST_APPLICATION_USER_PASSWORD = "TestPassword";
    short TEST_APPLICATION_USER_FAILED_LOGINS = 0;
    Boolean TEST_APPLICATION_USER_IS_ACTIVATED = false;
    Boolean TEST_APPLICATION_USER_IS_NOT_LOCKED = true;
    Address TEST_APPLICATION_USER_ADDRESS =
        Address.builder().country(TEST_ADDRESS_COUNTRY)
            .city(TEST_ADDRESS_CITY)
            .postalCode(TEST_ADDRESS_POSTAL_CODE)
            .street(TEST_ADDRESS_STREET)
            .streetNr(TEST_ADDRESS_STREETNR).build();
    UserRole TEST_APPLICATION_USER_ADMIN = UserRole.ADMIN;
    Long TEST_APPLICATION_USER_CREATE_FROM = 0L;
    int TEST_APPLICATION_USER_BONUS_POINTS = 250;
    String TEST_DETAILED_USER_DTO_FIRST_NAME = "FirstName";
    String TEST_DETAILED_USER_DTO_LAST_NAME = "LastName";
    String TEST_DETAILED_USER_DTO_EMAIL = "email@domain.com";
    UserRole TEST_DETAILED_USER_DTO_ADMIN = UserRole.ADMIN;

    String TEST_USER_LOGIN_DTO_EMAIL = "email@domain.com";
    String TEST_USER_LOGIN_DTO_PASSWORD = "TestPassword";
    int MAX_FAILED_LOGINS = 5;

    String TEST_RESET_PASSWORD_DTO_TOKEN = "token";
    String TEST_RESET_PASSWORD_DTO_PASSWORD = "newPassword";
    String TEST_RESET_PASSWORD_DTO_CONFIRM_PASSWORD = "newPassword";

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";

    String MERCHANDISE_BASE_URI = BASE_URI + "/merchandises";

    String REGISTER_BASE_URI = BASE_URI + "/registrations";

    String EVENTS_BASE_URI = BASE_URI + "/events";

    String PERFORMANCE_BASE_URI = BASE_URI + "/performances";

    String USER_BASE_URI = BASE_URI + "/users";

    String ARTIST_BASE_URI = BASE_URI + "/artists";

    String BOOKINGS_BASE_URI = BASE_URI + "/bookings";

    String PASSWORD_RESET_URI = BASE_URI + "/password-resets";

    String USER_LOGIN_BASE_URI = BASE_URI + "/authentications";

    String PURCHASE_BASE_URI = BASE_URI + "/purchases";

    String FOLDER_PATH = new File("src/main/resources/pictures/").getAbsolutePath() + "/";

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };
    MockMultipartFile TEST_MESSAGE = new MockMultipartFile("picture", "test_message_3.jpg", "image/jpg", new byte[10]);
    MockMultipartFile TEST_EVENT = new MockMultipartFile("image", "test_event_3.jpg", "image/jpg", new byte[10]);

    Picture PICTURE1 = Picture.builder().name("test3_cinema.jpg").build();

    Event TEST_CINEMA_EVENT = Event.builder()
        .title("CinemaEvent")
        .image(PICTURE1)
        .description("test-description")
        .startDate(LocalDate.of(2020, Month.JANUARY, 8))
        .endDate(LocalDate.of(2024, Month.JANUARY, 8))
        .durationInMinutes(120)
        .eventCategory(EventCategory.CINEMA)
        .build();

    HallPlan TEST_HALLPLAN = HallPlan.builder()
        .bookedNumOfStandingTickets(10)
        .description("test-description")
        .maxStandingCapacity(20)
        .name("test-hall")
        .build();

    Performance TEST_PERFORMANCE = Performance.builder()
        .date(LocalDate.of(2020, Month.JANUARY, 8))
        .name("TEST_PERFORMANCE")
        .startTime(LocalTime.of(13, 30))
        .endTime(LocalTime.of(13, 30).plusMinutes(TEST_CINEMA_EVENT.getDurationInMinutes()))
        .price(100.00)
        .build();
}
