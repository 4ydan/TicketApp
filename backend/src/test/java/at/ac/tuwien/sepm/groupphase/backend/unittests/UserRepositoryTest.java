package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest implements TestData {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenNothing_whenSaveApplicationUser_thenFindListWithOneElementAndFindApplicationUserById() {
        ApplicationUser applicationUser =
            ApplicationUser.builder()
                .id(ID)
                .firstName(TEST_APPLICATION_USER_FIRST_NAME)
                .lastName(TEST_APPLICATION_USER_LAST_NAME)
                .email(TEST_APPLICATION_USER_EMAIL)
                .password(TEST_APPLICATION_USER_PASSWORD)
                .failedLogins(TEST_APPLICATION_USER_FAILED_LOGINS)
                .isActivated(TEST_APPLICATION_USER_IS_ACTIVATED)
                .isNotLocked(TEST_APPLICATION_USER_IS_NOT_LOCKED)
                .address(TEST_APPLICATION_USER_ADDRESS)
                .role(TEST_APPLICATION_USER_ADMIN)
                .bonusPoints(TEST_APPLICATION_USER_BONUS_POINTS)
                .createFrom(TEST_APPLICATION_USER_CREATE_FROM)
                .build();

        userRepository.save(applicationUser);

        assertAll(
            () -> assertEquals(1, userRepository.findAll().size()),
            () -> assertNotNull(userRepository.findById(applicationUser.getId()))
        );
    }
}
