package spring.practice.elmenus_lite.service.impl;

import com.nimbusds.jose.JOSEException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.practice.elmenus_lite.dto.request.LoginRequest;
import spring.practice.elmenus_lite.dto.request.SignupRequest;
import spring.practice.elmenus_lite.dto.response.LoginResponse;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.enums.RoleName;
import spring.practice.elmenus_lite.enums.UserTypeName;
import spring.practice.elmenus_lite.exception.EmailAlreadyExistsException;
import spring.practice.elmenus_lite.model.*;
import spring.practice.elmenus_lite.repostory.CustomerRepository;
import spring.practice.elmenus_lite.repostory.UserRepository;
import spring.practice.elmenus_lite.repostory.UserTypeRepository;
import spring.practice.elmenus_lite.security.service.JwtService;
import spring.practice.elmenus_lite.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserTypeRepository userTypeRepository;
    private final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void signup(SignupRequest signUpRequest) {
        validateEmailIsAvailable(signUpRequest.email());

        UserType userTypeCustomer = getUserTypeCustomer();
        Role roleCustomer = getRoleCustomer();

        User user = new User()
                .setEmail(signUpRequest.email())
                .setPassword(passwordEncoder.encode(signUpRequest.password()))
                .setFirstName(signUpRequest.firstName())
                .setLastName(signUpRequest.lastName())
                .setFullName(signUpRequest.firstName() + " " + signUpRequest.lastName())
                .setUserType(userTypeCustomer);

        user.getRoles().add(roleCustomer);

        Customer customer = new Customer()
                .setUser(user);

        userRepository.save(user);
        customerRepository.save(customer);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        String jwt = jwtService.generateToken(authentication);

        return new LoginResponse(jwt);
    }

    private void validateEmailIsAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(ErrorMessage.EMAIL_ALREADY_EXISTS.getFinalMessage());
        }
    }

    private Role getRoleCustomer() {
        return roleRepository.findByName(RoleName.ROLE_CUSTOMER.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorMessage.ROLE_NOT_FOUND.getFinalMessage(RoleName.ROLE_CUSTOMER.getName())));
    }

    private UserType getUserTypeCustomer() {
        return userTypeRepository.findByName(UserTypeName.CUSTOMER.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        ErrorMessage.USER_TYPE_NOT_FOUND.getFinalMessage(UserTypeName.CUSTOMER.getName())));
    }
}
