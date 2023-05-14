package com.animoto.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class AuthResponse implements Serializable {

    private int userId;

    private  String jwtToken;
}
