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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final JwtUtil authUtils;


    public RegisterResponse registerUser(RegisterRequest registerRequest) {
       User user = userRepository.findByEmail(registerRequest.getEmail()).orElse(null);
        if(user != null){
            throw new IllegalArgumentException("user Already exists.");
        }
        user = userRepository.save(User.builder()
                        .name(registerRequest.getName())
                        .email(registerRequest.getEmail())
                        .password(passwordEncoder.encode(registerRequest.getPassword()))
                        .build()
                );
        return modelMapper.map(user, RegisterResponse.class);
    }

    public AuthResponse login(AuthRequest requestData) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestData.getEmail(),requestData.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = authUtils.generateToken(user);
        return new AuthResponse(token,user.getId(),user.getEmail());
    }
}