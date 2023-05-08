package com.animoto.services.interfaces;

import com.animoto.dto.requests.RegisterRequest;
import com.animoto.utils.exceptions.DuplicateDataException;

public interface UserService {
    void save(RegisterRequest registerRequest) throws DuplicateDataException;

}
