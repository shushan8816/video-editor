package com.animoto.dto.requests;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class LoginRequest  {

    @NotNull
    private String email;

    @NotNull
    private String password;

}
