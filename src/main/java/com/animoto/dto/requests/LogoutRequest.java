package com.animoto.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LogoutRequest {
    @NotNull
    private String token;

}
