package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationUserMappingTest implements TestData {

    private final ApplicationUser applicationUser =
        ApplicationUser.builder()
            .id(ID)
            .firstName(TEST_APPLICATION_USER_FIRST_NAME)
            .lastName(TEST_APPLICATION_USER_LAST_NAME)
            .email(TEST_APPLICATION_USER_EMAIL)
            .password(TEST_APPLICATION_USER_PASSWORD)
            .failedLogins(TEST_APPLICATION_USER_FAILED_LOGINS)
            .isActivated(TEST_APPLICATION_USER_IS_ACTIVATED)
            .address(TEST_APPLICATION_USER_ADDRESS)
            .role(TEST_APPLICATION_USER_ADMIN)
            .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
            .build();

    @Autowired
    private UserMapper userMapper;

    @Test
    public void givenNothing_whenMapDetailedUserDtoToEntity_thenEntityHasAllProperties() {
        DetailedUserDto detailedUserDto = userMapper.applicationUserToDetailedUserDto(applicationUser);
        assertAll(
            () -> assertEquals(ID, detailedUserDto.getId()),
            () -> assertEquals(TEST_APPLICATION_USER_FIRST_NAME, detailedUserDto.getFirstName()),
            () -> assertEquals(TEST_APPLICATION_USER_LAST_NAME, detailedUserDto.getLastName()),
            () -> assertEquals(TEST_APPLICATION_USER_EMAIL, detailedUserDto.getEmail()),
            () -> assertEquals(TEST_APPLICATION_USER_PASSWORD, detailedUserDto.getPassword()),
            () -> assertEquals(TEST_APPLICATION_USER_FAILED_LOGINS, detailedUserDto.getFailedLogins()),
            () -> assertEquals(TEST_DETAILED_USER_DTO_ADMIN, detailedUserDto.getRole())
        );
    }

    @Test
    public void givenNothing_whenMapEntityToDetailedUserDto_thenDtoHasAllProperties() {
        DetailedUserDto detailedUserDto = userMapper.applicationUserToDetailedUserDto(applicationUser);
        ApplicationUser applicationUser = userMapper.detailedUserDtoToApplicationUser(detailedUserDto);
        assertAll(
            () -> assertEquals(ID, applicationUser.getId()),
            () -> assertEquals(TEST_APPLICATION_USER_FIRST_NAME, applicationUser.getFirstName()),
            () -> assertEquals(TEST_APPLICATION_USER_LAST_NAME, applicationUser.getLastName()),
            () -> assertEquals(TEST_APPLICATION_USER_EMAIL, applicationUser.getEmail()),
            () -> assertEquals(TEST_APPLICATION_USER_PASSWORD, applicationUser.getPassword()),
            () -> assertEquals(TEST_APPLICATION_USER_FAILED_LOGINS, applicationUser.getFailedLogins()),
            () -> assertEquals(TEST_APPLICATION_USER_ADMIN, applicationUser.getRole())
        );
    }
}
