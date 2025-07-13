package spring.practice.elmenus_lite.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import spring.practice.elmenus_lite.dto.request.SignUpRequest;

public interface UserService extends UserDetailsService {
    void signUp(SignUpRequest signUpRequest);
}
