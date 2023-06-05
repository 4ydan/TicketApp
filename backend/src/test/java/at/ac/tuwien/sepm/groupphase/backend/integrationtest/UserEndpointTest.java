package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSessionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements TestData {

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

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;


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
            .bonusPoints(250)
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
    public void givenOneUserDto_whenFindByEmail_thenUserDtoWithAllProperties() throws Exception {
        userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(get
                (USER_BASE_URI + "/{email}", applicationUser.getEmail())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        UserSessionDto userSessionDto = objectMapper.readValue(response.getContentAsString(),
            UserSessionDto.class);

        assertEquals(applicationUser.getEmail(), userMapper.userSessionDtoToApplicationUser(userSessionDto).getEmail());
    }

    @Test
    public void givenOneUserDto_whenFindByActivatedEmail_thenNothingAndActivatedUserDtoWithAllProperties() throws Exception {
        userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(get
                (USER_BASE_URI + "/activated/{email}", applicationUser.getEmail())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(0, response.getContentAsString().length());

        applicationUser.setIsActivated(true);

        mvcResult = this.mockMvc.perform(get
                (USER_BASE_URI + "/activated/{email}", applicationUser.getEmail())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        response = mvcResult.getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    public void givenNothing_whenGetAllUsers_thenNoUsers() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get
                (USER_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<DetailedUserDto> detailedUserDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            DetailedUserDto[].class));

        assertEquals(0, detailedUserDtos.size());
    }

    @Test
    public void givenOneUser_whenGetAllUsers_thenOneUsers() throws Exception {
        ApplicationUser expectedUser = userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(get
                (USER_BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<ApplicationUser> users = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ApplicationUser[].class));

        assertEquals(1, users.size());
        ApplicationUser actualUser = users.get(0);
        assertAll(
            () -> assertEquals(expectedUser.getId(), actualUser.getId()),
            () -> assertEquals(expectedUser.getFirstName(), actualUser.getFirstName()),
            () -> assertEquals(expectedUser.getLastName(), actualUser.getLastName()),
            () -> assertEquals(expectedUser.getEmail(), actualUser.getEmail()),
            () -> assertEquals(expectedUser.getPassword(), actualUser.getPassword()),
            () -> assertEquals(expectedUser.getFailedLogins(), actualUser.getFailedLogins()),
            () -> assertEquals(expectedUser.getFailedLogins(), actualUser.getFailedLogins()),
            () -> assertEquals(expectedUser.getIsActivated(), actualUser.getIsActivated()),
            () -> assertEquals(expectedUser.getRole(), actualUser.getRole()),
            () -> assertEquals(expectedUser.getCreateFrom(), actualUser.getCreateFrom())
        );
    }

    @Test
    public void givenOneUser_whenDeleteUser_thenNoUserLeft() throws Exception {
        userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(delete
                (USER_BASE_URI + "/{email}", applicationUser.getEmail())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        userRepository.findUserByEmail(applicationUser.getEmail());
    }

    @Test
    public void givenOneUser_whenDeleteUser_thenNotFoundException() throws Exception {
        userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(delete
                (USER_BASE_URI + "/{email}", "USERNOTFOUND@email.com")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenOneUser_whenUpdateUser_thenUserUpdated() throws Exception {
        userRepository.save(applicationUser);
        DetailedUserDto detailedUserDto = DetailedUserDto.builder()
            .id(applicationUser.getId())
            .firstName("a")
            .lastName("a")
            .email(applicationUser.getEmail())
            .password("a")
            .failedLogins((short) 1)
            .isActivated(true)
            .address(AddressDto.builder().build())
            .role(UserRole.USER)
            .build();
        MvcResult mvcResult = this.mockMvc.perform(put
                (USER_BASE_URI + "/")
                .content(objectMapper.writeValueAsString(detailedUserDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(applicationUser.getEmail(), USER_ROLES)))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

    }

    @Test
    public void givenOneUnblockedUser_whenAuthorizedBlockUser_thenUserBlocked() throws Exception {
        userRepository.save(applicationUser);

        MvcResult mvcResult = this.mockMvc.perform(put
                (USER_BASE_URI + "/{email}",applicationUser.getEmail())
                .param("block","true")
                .accept(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        ApplicationUser actualUser = userRepository.findUserByEmail(applicationUser.getEmail());
        assertEquals(actualUser.getIsNotLocked(), false);

    }

    @Test
    public void givenOneBlockedUser_whenAuthorizedUnblockUser_thenUserUnblocked() throws Exception {
        ApplicationUser applicationUser1 = ApplicationUser.builder()
            .id(TEST_APPLICATION_USER_ID)
            .firstName(TEST_APPLICATION_USER_FIRST_NAME)
            .lastName(TEST_APPLICATION_USER_LAST_NAME)
            .email(TEST_APPLICATION_USER_EMAIL)
            .password(TEST_APPLICATION_USER_PASSWORD)
            .failedLogins(TEST_APPLICATION_USER_FAILED_LOGINS)
            .isActivated(TEST_APPLICATION_USER_IS_ACTIVATED)
            .isNotLocked(false)
            .address(TEST_APPLICATION_USER_ADDRESS)
            .role(TEST_APPLICATION_USER_ADMIN)
            .bonusPoints(TEST_APPLICATION_USER_BONUS_POINTS)
            .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
            .build();
        userRepository.save(applicationUser1);
        MvcResult mvcResult = this.mockMvc.perform(put
                (USER_BASE_URI + "/{email}",applicationUser.getEmail())
                .param("block","false")
                .accept(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        ApplicationUser actualUser = userRepository.findUserByEmail(applicationUser.getEmail());
        assertEquals(actualUser.getIsNotLocked(), true);
    }
}
