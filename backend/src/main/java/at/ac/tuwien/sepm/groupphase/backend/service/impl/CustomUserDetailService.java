package at.ac.tuwien.sepm.groupphase.backend.service.impl;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ChangePasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AddressMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;

import at.ac.tuwien.sepm.groupphase.backend.exception.PasswordMatchException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ConfirmationTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.type.UserRole;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.List;

@Slf4j
@Service
public class CustomUserDetailService implements UserService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public CustomUserDetailService(
        ConfirmationTokenRepository tokenRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenizer jwtTokenizer,
        UserRepository userRepository,
        UserValidator userValidator,
        UserMapper userMapper,
        AddressRepository addressRepository,
        AddressMapper addressMapper
    ) {
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;

        ApplicationUser user =
            (new ApplicationUser(null, "firstName", "lastName", "user@email.com", passwordEncoder.encode("userPassword"), (short) 0, true, true, new Address(null, "country", "city", 1L, "street", 2L), UserRole.USER, null, 250,
                null));
        ApplicationUser admin =
            (new ApplicationUser(null, "firstName", "lastName", "admin@email.com", passwordEncoder.encode("adminPassword"), (short) 0, true, true, new Address(null, "country", "city", 1L, "street", 2L), UserRole.ADMIN, null, 0,
                null));
        ApplicationUser vendor =
            (new ApplicationUser(null, "firstName", "lastName", "vendor@email.com", passwordEncoder.encode("vendorPassword"), (short) 0, true, true, new Address(null, "country", "city", 1L, "street", 2L), UserRole.VENDOR, null, 0,
                null));

        if (!userRepository.existsByEmail(user.getEmail())) {
            userRepository.save(user);
        }
        if (!userRepository.existsByEmail(admin.getEmail())) {
            userRepository.save(admin);
        }
        if (!userRepository.existsByEmail(vendor.getEmail())) {
            userRepository.save(vendor);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.trace("Load all user by email");
        try {
            ApplicationUser applicationUser = findActivatedApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getRole() == UserRole.ADMIN) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else if (applicationUser.getRole() == UserRole.VENDOR) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_VENDOR", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), true, true, true, applicationUser.getIsNotLocked(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findActivatedApplicationUserByEmail(String email) {
        log.trace("Find activated application user by email");
        return userRepository.findUserByEmailAndIsActivatedTrue(email);
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        log.trace("Find application user by email");
        ApplicationUser user = userRepository.findUserByEmail(email);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
    }

    @Override
    public ApplicationUser findActivatedCustomerByEmail(String email) {
        log.trace("Find activated customer user by email");
        return userRepository.findUserByEmailAndIsActivatedTrueAndRoleIs0(email);
    }

    @Override
    public DetailedUserDto createUser(DetailedUserDto user) throws ValidationException, ConflictException {
        log.trace("Create User {}", user.getEmail());
        userValidator.validateForAdd(user);
        String encodedPw = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPw);
        return userMapper.applicationUserToDetailedUserDto(userRepository.save(userMapper.detailedUserDtoToApplicationUser(user)));
    }

    @Override
    public void deleteUser(String email) {
        log.trace("Delete User {}", email);
        ApplicationUser user = findApplicationUserByEmail(email);
        tokenRepository.deleteAllByUser_Id(user.getId());
        userRepository.delete(user);
    }

    @Override
    public List<DetailedUserDto> getAllUsers() {
        log.trace("Get all users");
        return userRepository.findAll().stream().map(userMapper::applicationUserToDetailedUserDto).toList();
    }

    @Override
    public void blockUser(String email) {
        log.trace("Block user {}", email);
        ApplicationUser user;
        try {
            user = findApplicationUserByEmail(email);
            user.setFailedLogins((short) 5);
            user.setIsNotLocked(false);
        } catch (NotFoundException e) {
            throw new NotFoundException("User could not be found!");
        }
        userRepository.save(user);
    }

    @Override
    public void unblockUser(String email) {
        log.trace("Unblock user {}", email);
        ApplicationUser user;
        try {
            user = findApplicationUserByEmail(email);
            user.setFailedLogins((short) 0);
            user.setIsNotLocked(true);
        } catch (NotFoundException e) {
            throw new NotFoundException("User could not be found!");
        }
        userRepository.save(user);
    }


    @Override
    public ApplicationUser editUser(DetailedUserDto detailedUserDto) {
        log.trace("Update User {}", detailedUserDto);
        ApplicationUser user = findApplicationUserByEmail(detailedUserDto.getEmail());
        boolean isUpdated = false;
        Long oldAddressId = 0L;


        if (!user.getFirstName().equals(detailedUserDto.getFirstName()) && StringUtils.hasLength(detailedUserDto.getFirstName())) {
            user.setFirstName(detailedUserDto.getFirstName());
            isUpdated = true;
        }

        if (!user.getLastName().equals(detailedUserDto.getLastName()) && StringUtils.hasLength(detailedUserDto.getLastName())) {
            user.setLastName(detailedUserDto.getLastName());
            isUpdated = true;
        }

        if (user.getAddress() == null) {
            Address address = addressRepository.save(addressMapper.addressDtoToAddress(detailedUserDto.getAddress()));
            user.setAddress(address);
            isUpdated = true;
        } else if (!user.getAddress().equals(addressMapper.addressDtoToAddress(detailedUserDto.getAddress()))) {
            oldAddressId = user.getAddress().getId();
            Address address = null;
            if (detailedUserDto.getAddress() != null) {
                address = addressRepository.save(addressMapper.addressDtoToAddress(detailedUserDto.getAddress()));
            }
            user.setAddress(address);
            isUpdated = true;
        }

        if (isUpdated) {
            userRepository.save(user);
            if (oldAddressId != 0L) {
                addressRepository.deleteById(oldAddressId);
            }
        }
        return user;
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        log.trace("Change password {}", changePasswordDto);
        ApplicationUser user = findApplicationUserByEmail(changePasswordDto.getEmail());

        boolean isMatch = passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword());

        if (isMatch) {
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        } else {
            throw new PasswordMatchException("Old password does not match");
        }
        userRepository.save(user);
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        log.trace("Login User ({})", userLoginDto.getEmail());

        if (findActivatedApplicationUserByEmail(userLoginDto.getEmail()) != null) {
            UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
            ) {
                ApplicationUser user = findActivatedApplicationUserByEmail(userLoginDto.getEmail());
                if (passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())) {
                    user.setFailedLogins((short) 0);
                    userRepository.save(user);

                    List<String> roles = userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();
                    return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
                } else {
                    short failedLogins = user.getFailedLogins();
                    user.setFailedLogins((short) ((failedLogins) + 1));
                    userRepository.save(user);

                    if (user.getFailedLogins() >= MAX_FAILED_ATTEMPTS) {
                        if (user.getIsNotLocked()) {
                            user.setIsNotLocked(false);
                            userRepository.save(user);
                        }
                    }
                }
            }
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    public int activateUser(String email) {
        log.trace("Activate user with email {}", email);
        return userRepository.activate(email);
    }

    @Override
    public void lockUser(ApplicationUser user) {
        log.trace("Lock User {}", user);
        user.setIsActivated(false);
        userRepository.save(user);
    }
}
