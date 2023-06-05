package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandisePurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.entity.Picture;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.UserRole;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PurchaseEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchandiseRepository merchandiseRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private Merchandise merchandise;

    private final Picture picture = Picture.builder().name("test1.jpg").build();

    private ApplicationUser applicationUser;

    @BeforeEach
    public void beforeEach() {
        merchandiseRepository.deleteAll();
        confirmationTokenRepository.deleteAll();
        userRepository.deleteAll();
        merchandise = Merchandise.builder()
            .name(TEST_MERCHANDISE_NAME)
            .price(TEST_MERCHANDISE_PRICE)
            .inStock(TEST_MERCHANDISE_IN_STOCK)
            .costsPoints(TEST_MERCHANDISE_COSTS_POINTS)
            .picture(picture)
            .build();
        applicationUser =
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
                .role(UserRole.USER)
                .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
                .build();
    }

    @Test
    public void givenOneMerchandise_whenPurchaseMerchandise_then201() throws Exception {
        userRepository.save(applicationUser);
        merchandiseRepository.save(merchandise);

        MerchandisePurchaseDto merchandisePurchaseDto = new MerchandisePurchaseDto(merchandise.getId(), TEST_MERCHANDISE_VALID_AMOUNT);
        List<MerchandisePurchaseDto> merchandisePurchaseDtos = new ArrayList<>();
        merchandisePurchaseDtos.add(merchandisePurchaseDto);
        PurchaseDto purchaseDto = new PurchaseDto();
        purchaseDto.setMerchandises(merchandisePurchaseDtos);
        MvcResult mvcResult = this.mockMvc.perform(post(PURCHASE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void givenOneMerchandise_whenPurchaseMerchandiseInvalidAmount_then422() throws Exception {
        merchandiseRepository.save(merchandise);
        userRepository.save(applicationUser);

        MerchandisePurchaseDto merchandisePurchaseDto = new MerchandisePurchaseDto(merchandise.getId(), TEST_MERCHANDISE_INVALID_AMOUNT);
        List<MerchandisePurchaseDto> merchandisePurchaseDtos = new ArrayList<>();
        merchandisePurchaseDtos.add(merchandisePurchaseDto);
        PurchaseDto purchaseDto = new PurchaseDto();
        purchaseDto.setMerchandises(merchandisePurchaseDtos);
        MvcResult mvcResult = this.mockMvc.perform(post(PURCHASE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenOneMerchandise_whenPurchaseMerchandiseInvalidId_then422() throws Exception {
        merchandiseRepository.save(merchandise);
        userRepository.save(applicationUser);

        MerchandisePurchaseDto merchandisePurchaseDto = new MerchandisePurchaseDto(TEST_MERCHANDISE_INVALID_ID, TEST_MERCHANDISE_VALID_AMOUNT);
        List<MerchandisePurchaseDto> merchandisePurchaseDtos = new ArrayList<>();
        merchandisePurchaseDtos.add(merchandisePurchaseDto);
        PurchaseDto purchaseDto = new PurchaseDto();
        purchaseDto.setMerchandises(merchandisePurchaseDtos);
        MvcResult mvcResult = this.mockMvc.perform(post(PURCHASE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenOneMerchandise_whenPurchaseMerchandiseAccountLocked_then423() throws Exception {
        merchandiseRepository.save(merchandise);
        applicationUser.setIsNotLocked(false);
        userRepository.save(applicationUser);

        MerchandisePurchaseDto merchandisePurchaseDto = new MerchandisePurchaseDto(TEST_MERCHANDISE_INVALID_ID, TEST_MERCHANDISE_VALID_AMOUNT);
        List<MerchandisePurchaseDto> merchandisePurchaseDtos = new ArrayList<>();
        merchandisePurchaseDtos.add(merchandisePurchaseDto);
        PurchaseDto purchaseDto = new PurchaseDto();
        purchaseDto.setMerchandises(merchandisePurchaseDtos);
        MvcResult mvcResult = this.mockMvc.perform(post(PURCHASE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(purchaseDto))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(TEST_APPLICATION_USER_EMAIL, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.LOCKED.value(), response.getStatus());
    }
}
