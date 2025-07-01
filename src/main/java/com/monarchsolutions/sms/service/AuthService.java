package com.monarchsolutions.sms.service;

import com.monarchsolutions.sms.dto.common.UserLoginDTO;
import com.monarchsolutions.sms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public UserLoginDTO getUserByUsernameOrEmail(String usernameOrEmail, String language) {
        return userRepository.getUserByUsernameOrEmail(usernameOrEmail, language);
    }
}
