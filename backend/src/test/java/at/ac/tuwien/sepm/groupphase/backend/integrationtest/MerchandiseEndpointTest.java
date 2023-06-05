package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseRedeemDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.PictureFileManager;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MerchandiseEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private PictureFileManager pictureFileManager;

    @Autowired
    private SecurityProperties securityProperties;

    private Merchandise merchandise;

    private ApplicationUser applicationUser;

    private final Picture picture = Picture.builder().name("test1.jpg").build();

    @BeforeEach
    public void beforeEach() {
        merchandiseRepository.deleteAll();
        merchandise = Merchandise.builder()
            .name(TEST_MERCHANDISE_NAME)
            .price(TEST_MERCHANDISE_PRICE)
            .inStock(TEST_MERCHANDISE_IN_STOCK)
            .costsPoints(TEST_MERCHANDISE_COSTS_POINTS)
            .picture(picture)
            .build();
        userRepository.deleteAll();
        applicationUser = ApplicationUser.builder()
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
            .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
            .bonusPoints(TEST_APPLICATION_USER_BONUS_POINTS)
            .build();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(MERCHANDISE_BASE_URI + "?page=0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<MerchandiseDto> merchandisesDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            MerchandiseDto[].class));

        assertEquals(0, merchandisesDtos.size());
    }

    @Test
    public void givenOneMerchandise_whenFindAll_thenListWithSizeOneAndMerchandiseWithAllProperties()
        throws Exception {
        merchandiseRepository.save(merchandise);

        MvcResult mvcResult = this.mockMvc.perform(get(MERCHANDISE_BASE_URI + "?page=0")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<MerchandiseDto> merchandiseDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            MerchandiseDto[].class));

        assertEquals(1, merchandiseDtos.size());
        MerchandiseDto merchandiseDto = merchandiseDtos.get(0);
        assertAll(
            () -> assertEquals(merchandise.getId(), merchandiseDto.getId()),
            () -> assertEquals(TEST_MERCHANDISE_NAME, merchandiseDto.getName()),
            () -> assertEquals(TEST_MERCHANDISE_PRICE, merchandiseDto.getPrice()),
            () -> assertEquals(TEST_MERCHANDISE_IN_STOCK, merchandiseDto.getInStock()),
            () -> assertEquals(TEST_MERCHANDISE_COSTS_POINTS, merchandiseDto.getCostsPoints()),
            () -> assertArrayEquals(pictureFileManager.getPictureByName(TEST_NEWS_PICTURE_NAME), merchandiseDto.getPicture())
        );
    }

    @Test
    public void givenOneMerchandise_whenRedeemMerchandise_then200() throws Exception {
        Long id = userRepository.save(applicationUser).getId();
        merchandiseRepository.save(merchandise);

        MerchandiseRedeemDto merchandiseRedeemDto = new MerchandiseRedeemDto(id, merchandise.getId(), TEST_MERCHANDISE_VALID_AMOUNT);

        MvcResult mvcResult = this.mockMvc.perform(put(MERCHANDISE_BASE_URI + "/bonusPoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(merchandiseRedeemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void givenOneMerchandise_whenRedeemMerchandiseInvalidAmount_then422() throws Exception {
        applicationUser.setBonusPoints(100000);
        Long id = userRepository.save(applicationUser).getId();
        merchandiseRepository.save(merchandise);

        MerchandiseRedeemDto merchandiseRedeemDto = new MerchandiseRedeemDto(id, merchandise.getId(), TEST_MERCHANDISE_INVALID_AMOUNT);

        MvcResult mvcResult = this.mockMvc.perform(put(MERCHANDISE_BASE_URI + "/bonusPoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(merchandiseRedeemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenOneMerchandise_whenRedeemMerchandiseInsufficientBonusPoints_then422() throws Exception {
        applicationUser.setBonusPoints(0);
        Long id = userRepository.save(applicationUser).getId();
        merchandiseRepository.save(merchandise);

        MerchandiseRedeemDto merchandiseRedeemDto = new MerchandiseRedeemDto(id, merchandise.getId(), TEST_MERCHANDISE_VALID_AMOUNT);

        MvcResult mvcResult = this.mockMvc.perform(put(MERCHANDISE_BASE_URI + "/bonusPoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(merchandiseRedeemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenOneMerchandise_whenRedeemMerchandiseInvalidId_then422() throws Exception {
        Long id = userRepository.save(applicationUser).getId();
        merchandiseRepository.save(merchandise);

        MerchandiseRedeemDto merchandiseRedeemDto = new MerchandiseRedeemDto(id, TEST_MERCHANDISE_INVALID_ID, TEST_MERCHANDISE_VALID_AMOUNT);

        MvcResult mvcResult = this.mockMvc.perform(put(MERCHANDISE_BASE_URI + "/bonusPoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(merchandiseRedeemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenOneMerchandise_whenRedeemMerchandiseInvalidUserId_then422() throws Exception {
        userRepository.save(applicationUser);
        merchandiseRepository.save(merchandise);

        MerchandiseRedeemDto merchandiseRedeemDto = new MerchandiseRedeemDto(TEST_APPLICATION_USER_INVALID_ID, merchandise.getId(), TEST_MERCHANDISE_VALID_AMOUNT);

        MvcResult mvcResult = this.mockMvc.perform(put(MERCHANDISE_BASE_URI + "/bonusPoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(merchandiseRedeemDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

}
