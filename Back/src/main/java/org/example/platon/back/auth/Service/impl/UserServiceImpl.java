package org.example.platon.back.auth.Service.impl;

import org.example.platon.back.auth.Service.UserService;
import org.example.platon.back.auth.dto.AuthResponse;
import org.example.platon.back.auth.dto.LoginRequest;
import org.example.platon.back.auth.dto.RegistrationRequest;
import org.example.platon.back.auth.model.Role;
import org.example.platon.back.auth.model.UserEntity;
import org.example.platon.back.auth.repository.UserRepository;
import org.example.platon.back.auth.security.JwtService;
import org.example.platon.back.auth.security.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthResponse registerUser(RegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        UserEntity newUser = new UserEntity(
                null,
                request.name(),
                request.lastName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Set.of(Role.USER)
        );

        userRepository.save(newUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getEmail());
        return new AuthResponse(jwtService.generateToken(userDetails), Role.USER.name());
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = user.getRole().stream().findFirst().map(Role::name).orElse(Role.USER.name());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        return new AuthResponse(jwtService.generateToken(userDetails), role);
    }
}
