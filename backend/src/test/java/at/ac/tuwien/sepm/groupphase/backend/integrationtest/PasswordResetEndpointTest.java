package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PasswordResetService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PasswordResetEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordResetService resetService;


    private ResetPasswordDto resetPasswordDto =
        ResetPasswordDto.builder()
            .token(TEST_RESET_PASSWORD_DTO_TOKEN)
            .password(TEST_RESET_PASSWORD_DTO_PASSWORD)
            .confirmPassword(TEST_RESET_PASSWORD_DTO_CONFIRM_PASSWORD)
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

        resetPasswordDto =
            ResetPasswordDto.builder()
                .token(TEST_RESET_PASSWORD_DTO_TOKEN)
                .password(TEST_RESET_PASSWORD_DTO_PASSWORD)
                .confirmPassword(TEST_RESET_PASSWORD_DTO_CONFIRM_PASSWORD)
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
    public void givenRegisteredUser_whenRequestPasswordReset_SendConfirmationMessage() throws Exception {

        applicationUser.setIsActivated(true);
        userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(PASSWORD_RESET_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(applicationUser.getEmail())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(content);
    }

    @Test
    public void givenRegisteredUser_whenRequestAndUpdatePassword_SendConfirmationMessage() throws Exception {
        applicationUser.setIsActivated(true);
        ApplicationUser user = userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(PASSWORD_RESET_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn();

        String token = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(token);

        resetPasswordDto.setToken(token);
        String body = objectMapper.writeValueAsString(resetPasswordDto);

        mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.put(PASSWORD_RESET_URI + "/updatePassword")
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andReturn();

        String message = mvcResult.getResponse().getContentAsString();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(message, "Password update successful");
    }

    @Test
    public void givenNothing_whenRequestPasswordReset_then422() throws Exception {

        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(PASSWORD_RESET_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(applicationUser.getEmail())
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
            .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(content);
    }

    @Test
    public void givenDtoWithInvalidToken_whenUpdatePassword_then410() throws Exception {

        String body = objectMapper.writeValueAsString(resetPasswordDto);


        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.put(PASSWORD_RESET_URI + "/updatePassword")
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isGone())
            .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.GONE.value(), response.getStatus());
        assertNotNull(content);
    }

    @Test
    public void givenDtoWithInvalidPassword_whenUpdatePassword_then422() throws Exception {
        applicationUser.setIsActivated(true);
        ApplicationUser user = userRepository.save(applicationUser);
        String token = resetService.requestPasswordReset(user.getEmail());

        resetPasswordDto.setToken(token);
        resetPasswordDto.setPassword("invalid");
        resetPasswordDto.setConfirmPassword("invalid");
        String body = objectMapper.writeValueAsString(resetPasswordDto);


        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.put(PASSWORD_RESET_URI + "/updatePassword")
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isUnprocessableEntity())
            .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertNotNull(content);
    }


}
