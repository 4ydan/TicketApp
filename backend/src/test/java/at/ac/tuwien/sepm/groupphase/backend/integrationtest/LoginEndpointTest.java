package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoginEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    private UserLoginDto userLoginDto =
        UserLoginDto.builder()
            .email(TEST_USER_LOGIN_DTO_EMAIL)
            .password(TEST_USER_LOGIN_DTO_PASSWORD)
            .build();

    private ApplicationUser applicationUser =
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
            .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
            .build();

    @BeforeEach
    public void beforeEach() {
        confirmationTokenRepository.deleteAll();
        userRepository.deleteAll();

        userLoginDto =
            UserLoginDto.builder()
                .email(TEST_USER_LOGIN_DTO_EMAIL)
                .password(TEST_USER_LOGIN_DTO_PASSWORD)
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
                .role(TEST_APPLICATION_USER_ADMIN)
                .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
                .build();
    }

    @Test
    public void givenOneAppUser_whenLogin_thenReturnAuthToken() throws Exception {
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        applicationUser.setIsActivated(true);
        userRepository.save(applicationUser);

        String body = objectMapper.writeValueAsString(userLoginDto);

        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(USER_LOGIN_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assertNotNull(content);
    }

    @Test
    public void givenOneAppUser_whenWrongPassword5Times_thenAppUserIsLocked() throws Exception {
        userLoginDto.setPassword("wrongPassword");
        applicationUser.setIsActivated(true);
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));

        userRepository.save(applicationUser);

        String body = objectMapper.writeValueAsString(userLoginDto);

        MvcResult mvcResult;

        for (int i = 1; i <= MAX_FAILED_LOGINS; i++) {
            mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post(USER_LOGIN_BASE_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(body)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized())
                .andReturn();

            MockHttpServletResponse response = mvcResult.getResponse();


            assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

            assertEquals(response.getContentAsString(), "Username or password is incorrect or account is locked");
        }
        ApplicationUser user = userRepository.findUserByEmailAndIsActivatedTrue(applicationUser.getEmail());

        assertEquals(user.getIsNotLocked(), false);

        userLoginDto.setPassword(passwordEncoder.encode(applicationUser.getPassword()));

        body = objectMapper.writeValueAsString(userLoginDto);

        mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(USER_LOGIN_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus()),
            () -> assertEquals(response.getContentAsString(), "Username or password is incorrect or account is locked")
        );
    }

    @Test
    public void givenOneAppUser_whenWrongUsernameAndPassword_then401() throws Exception {
        userLoginDto.setEmail("wrongEmail");
        userLoginDto.setPassword("wrongPassword");
        applicationUser.setIsActivated(true);
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        userRepository.save(applicationUser);

        String body = objectMapper.writeValueAsString(userLoginDto);

        MvcResult mvcResult;

        mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(USER_LOGIN_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus()),
            () -> assertEquals(response.getContentAsString(), "Username or password is incorrect or account is locked")
        );
    }


}
