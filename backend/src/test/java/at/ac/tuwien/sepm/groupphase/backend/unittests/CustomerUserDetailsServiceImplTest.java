package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangePasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AddressMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.PasswordMatchException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CustomerUserDetailsServiceImplTest implements TestData {

    private final Address address =
        Address.builder()
            .id(ID)
            .country(TEST_ADDRESS_COUNTRY)
            .city(TEST_ADDRESS_CITY)
            .postalCode(TEST_ADDRESS_POSTAL_CODE)
            .street(TEST_ADDRESS_STREET)
            .streetNr(TEST_ADDRESS_STREETNR).build();

    private final AddressDto addressDto = AddressDto.builder()
        .country(TEST_ADDRESS_COUNTRY)
        .city(TEST_ADDRESS_CITY)
        .postalCode(TEST_ADDRESS_POSTAL_CODE)
        .street(TEST_ADDRESS_STREET)
        .streetNr(TEST_ADDRESS_STREETNR)
        .build();

    private final ApplicationUser user = ApplicationUser.builder()
        .id(ID)
        .firstName(TEST_APPLICATION_USER_FIRST_NAME)
        .lastName(TEST_APPLICATION_USER_LAST_NAME)
        .password(TEST_APPLICATION_USER_PASSWORD)
        .email(TEST_APPLICATION_USER_EMAIL)
        .role(TEST_APPLICATION_USER_ADMIN)
        .address(address)
        .isActivated(TEST_APPLICATION_USER_IS_ACTIVATED)
        .build();

    private final DetailedUserDto detailedUserDto = DetailedUserDto.builder()
        .id(ID)
        .firstName(TEST_DETAILED_USER_DTO_FIRST_NAME)
        .lastName(TEST_DETAILED_USER_DTO_LAST_NAME)
        .password(TEST_APPLICATION_USER_PASSWORD)
        .email(TEST_DETAILED_USER_DTO_EMAIL)
        .role(TEST_DETAILED_USER_DTO_ADMIN)
        .address(addressDto)
        .isActivated(TEST_APPLICATION_USER_IS_ACTIVATED)
        .build();

    private final ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
        .email(TEST_APPLICATION_USER_EMAIL)
        .oldPassword("OldPassword")
        .newPassword("NewPassword")
        .build();

    private CustomUserDetailService service;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    UserValidator userValidator;
    @Mock
    UserMapper userMapper;
    @Mock
    private JwtTokenizer jwtTokenizer;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    private AddressRepository addressRepository;

    @BeforeEach
    public void setup() {
        service = new CustomUserDetailService(
            confirmationTokenRepository,
            passwordEncoder,
            jwtTokenizer,
            userRepository,
            userValidator,
            userMapper,
            addressRepository,
            addressMapper
        );
    }

    @Test
    void changePassword_throwsPasswordMatchException() {
        ApplicationUser applicationUser = user;
        Mockito.when(userRepository.findUserByEmail(Mockito.anyString())).thenReturn(applicationUser);
        assertThrows(PasswordMatchException.class, () -> service.changePassword(changePasswordDto));
    }

    @Test
    void testDeleteUser() {
        ApplicationUser applicationUser = user;
        Mockito.when(userRepository.findUserByEmail(Mockito.anyString())).thenReturn(applicationUser);
        service.deleteUser("email");
        Mockito.verify(userRepository).delete(applicationUser);
    }

    @Test
    void testDeleteUser_NotFound() {
        Mockito.when(userRepository.findUserByEmail(Mockito.anyString())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> service.deleteUser("email"));
    }

    @Test
    void updateUser_Success() {
        Mockito.when(userRepository.findUserByEmail(Mockito.anyString())).thenReturn(user);
        Mockito.when(addressRepository.save(Mockito.any())).thenReturn(address);
        DetailedUserDto detailedUser = detailedUserDto;
        detailedUser.setLastName("NewLastName");
        service.editUser(detailedUser);
        Mockito.verify(userRepository, times(4)).save(Mockito.any());
    }

    @Test
    void updateUser_Success_NoChange() {
        Mockito.when(userRepository.findUserByEmail(Mockito.anyString())).thenReturn(user);
        Mockito.when(addressMapper.addressDtoToAddress(addressDto)).thenReturn(address);
        service.editUser(detailedUserDto);
        Mockito.verify(userRepository, times(3)).save(Mockito.any());
    }

    @Test
    void blockUser() {
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        service.blockUser(user.getEmail());
        assertEquals(5, user.getFailedLogins());
        assertEquals(false, user.getIsNotLocked());
        Mockito.verify(userRepository, Mockito.times(4)).save(Mockito.any());
    }

    @Test
    void unblockUser() {
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        service.unblockUser(user.getEmail());
        assertEquals(0, user.getFailedLogins());
        assertEquals(true, user.getIsNotLocked());
        Mockito.verify(userRepository, Mockito.times(4)).save(Mockito.any());
    }
}