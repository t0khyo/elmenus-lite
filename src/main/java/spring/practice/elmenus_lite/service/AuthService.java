package spring.practice.elmenus_lite.service;

import com.nimbusds.jose.JOSEException;
import spring.practice.elmenus_lite.dto.request.LoginRequest;
import spring.practice.elmenus_lite.dto.request.SignupRequest;
import spring.practice.elmenus_lite.dto.response.LoginResponse;

public interface AuthService {
    void signup(SignupRequest signUpRequest);

    LoginResponse login(LoginRequest loginRequest);
}
