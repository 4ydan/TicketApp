package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import at.ac.tuwien.sepm.groupphase.backend.entity.ConfirmationToken;
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

import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RegisterEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;


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
            .bonusPoints(0)
            .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
            .build();

    @BeforeEach
    public void beforeEach() {
        confirmationTokenRepository.deleteAll();
        userRepository.deleteAll();

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
                .bonusPoints(0)
                .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
                .build();
    }


    @Test
    public void givenNothing_whenRegister_thenReturnToken() throws Exception {
        DetailedUserDto detailedUserDto = userMapper.applicationUserToDetailedUserDto(applicationUser);
        String body = objectMapper.writeValueAsString(detailedUserDto);

        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(REGISTER_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assertNotNull(content);
    }

    @Test
    public void givenNothing_whenRegisterAndConfirmed_thenReturnConfirmationMessage() throws Exception {
        DetailedUserDto detailedUserDto = userMapper.applicationUserToDetailedUserDto(applicationUser);
        String body = objectMapper.writeValueAsString(detailedUserDto);

        MvcResult mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.post(REGISTER_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
            .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        assertNotNull(content);

        ConfirmationToken stored = confirmationTokenRepository.findConfirmationTokenByUser_Id(applicationUser.getId());

        mvcResult = this.mockMvc
            .perform(MockMvcRequestBuilders.get(REGISTER_BASE_URI + "/confirmation")
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", stored.getToken()))
            .andExpect(status().isOk()).andReturn();

        content = mvcResult.getResponse().getContentAsString();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(content);
    }
}
