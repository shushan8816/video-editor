package com.animoto.services.interfaces;

import com.animoto.dto.requests.LoginRequest;
import com.animoto.dto.requests.LogoutRequest;
import com.animoto.utils.exceptions.JwtAuthenticationException;

import java.util.Map;

public interface AuthService {

    Map<String, Object> authenticate(LoginRequest loginRequest) throws JwtAuthenticationException;

    void loggedOut(LogoutRequest logoutRequest);
}
