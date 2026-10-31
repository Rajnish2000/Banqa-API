package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.dtos.AuthRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.AuthResponse;
import com.rajnish.bankmanagementRESTAPI.dtos.RegisterRequest;
import com.rajnish.bankmanagementRESTAPI.dtos.RegisterResponse;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.jwt.JwtUtil;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtUtil authUtils;


    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("user Already exists.");
        }
        User user = userRepository.save(User.builder()
                        .name(registerRequest.getName())
                        .email(email)
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .build()
                );
        return modelMapper.map(user, RegisterResponse.class);
    }

    public AuthResponse login(AuthRequest requestData) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestData.getEmail().trim().toLowerCase(Locale.ROOT), requestData.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtils.generateToken(user);
        return new AuthResponse(token,user.getId(),user.getEmail());
    }
}