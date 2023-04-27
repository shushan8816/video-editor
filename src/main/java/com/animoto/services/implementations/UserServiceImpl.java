package com.animoto.services.implementations;

import com.animoto.dto.requests.RegisterRequest;
import com.animoto.models.User;
import com.animoto.repositories.UserRepository;
import com.animoto.services.interfaces.UserService;
import com.animoto.utils.exeptions.DuplicateDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public void save(RegisterRequest registerRequest) throws DuplicateDataException {
        String email = registerRequest.getEmail();

        if (userRepository.getByEmail(registerRequest.getEmail()).isPresent()) {
            log.info("An account with email: {} already exists", email);
            throw new DuplicateDataException("An account with current email already exists");
        }

        User user = new User();

        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        log.info("Saving new user to the database");
        userRepository.save(user);
    }
}
