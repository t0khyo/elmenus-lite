package spring.practice.elmenus_lite.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.practice.elmenus_lite.enums.ErrorMessage;
import spring.practice.elmenus_lite.enums.RoleName;
import spring.practice.elmenus_lite.exception.EmailAlreadyExistsException;
import spring.practice.elmenus_lite.model.Role;
import spring.practice.elmenus_lite.model.RoleRepository;
import spring.practice.elmenus_lite.model.User;
import spring.practice.elmenus_lite.repostory.UserRepository;
import spring.practice.elmenus_lite.service.UserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.getUserByEmail(username);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.EMAIL_NOT_FOUND.getFinalMessage()));
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
}
