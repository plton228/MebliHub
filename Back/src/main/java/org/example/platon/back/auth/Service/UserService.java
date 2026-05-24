package org.example.platon.back.auth.Service;

import org.example.platon.back.auth.dto.AuthResponse;
import org.example.platon.back.auth.dto.LoginRequest;
import org.example.platon.back.auth.dto.RegistrationRequest;

public interface UserService {
    AuthResponse registerUser(RegistrationRequest request);
    AuthResponse loginUser(LoginRequest request);
}
