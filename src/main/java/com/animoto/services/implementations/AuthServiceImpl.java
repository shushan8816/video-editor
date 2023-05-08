package com.animoto.services.implementations;

import com.animoto.dto.requests.LoginRequest;
import com.animoto.dto.requests.LogoutRequest;
import com.animoto.models.User;
import com.animoto.repositories.UserRepository;
import com.animoto.security.JwtTokenProvider;
import com.animoto.services.interfaces.AuthService;
import com.animoto.utils.exceptions.JwtAuthenticationException;
import com.animoto.utils.helper.JwtTokenCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtTokenCache jwtTokenCache;


    @Override
    public Map<String, Object> authenticate(LoginRequest loginRequest) throws JwtAuthenticationException {
        String email, password;
        int userId;

        email = loginRequest.getEmail();
        User user = userRepository.getByEmail(email);

        password = loginRequest.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.info("Password is not valid");
            throw new JwtAuthenticationException("Incorrect email or password", HttpStatus.UNAUTHORIZED);
        }

        userId = user.getId();
        String token = jwtTokenProvider.generateToken(userId, password, email);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", userId);
        response.put("token", token);

        return response;
    }

    @Override
    public void loggedOut(LogoutRequest logoutRequest) {
        String token = logoutRequest.getToken();
        jwtTokenCache.markLogoutToken(token);
    }
}
