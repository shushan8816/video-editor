package com.animoto.dto.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class TrimMediaFileRequest {

    @Positive
    private int fileId;

    @NotNull
    private String startTime;

    @NotNull
    private String endTime;
}
