package com.animoto.controllers;

import com.animoto.dto.requests.LoginRequest;
import com.animoto.dto.requests.LogoutRequest;
import com.animoto.dto.requests.RegisterRequest;
import com.animoto.models.User;
import com.animoto.services.interfaces.AuthService;
import com.animoto.services.interfaces.UserService;
import com.animoto.utils.exceptions.DuplicateDataException;
import com.animoto.utils.exceptions.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest request) throws JwtAuthenticationException {
        return ResponseEntity.ok(authService.authenticate(request));

    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterRequest registerRequest) throws DuplicateDataException {
        userService.save(registerRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogoutRequest logoutRequest) {
        authService.loggedOut(logoutRequest);
        return ResponseEntity.ok().build();
    }

}
