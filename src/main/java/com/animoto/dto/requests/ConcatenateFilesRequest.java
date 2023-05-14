package com.animoto.dto.requests;

import lombok.Data;

import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class ConcatenateFilesRequest {

    @Positive
    private List<Integer> fileIds;
}
