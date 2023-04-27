package com.animoto.services.interfaces;

import com.animoto.dto.requests.RegisterRequest;
import com.animoto.models.User;
import com.animoto.utils.exeptions.DuplicateDataException;

public interface UserService {
    void save(RegisterRequest registerRequest) throws DuplicateDataException;

}
