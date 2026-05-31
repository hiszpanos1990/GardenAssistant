package com.example.gardenassistant.security;

import com.example.gardenassistant.exception.AlreadyExistException;
import com.example.gardenassistant.exception.InvalidCredentialException;
import com.example.gardenassistant.exception.UserNotFoundException;
import com.example.gardenassistant.garden.dto.AuthResponse;
import com.example.gardenassistant.garden.dto.LoginRequest;
import com.example.gardenassistant.garden.dto.RegisterRequest;
import com.example.gardenassistant.garden.entity.Roles;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.UserRepository;
import com.example.gardenassistant.garden.service.CustomUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserService userService;

    public AuthResponse register(RegisterRequest registerRequest){
        if (userRepository.existsByEmail(registerRequest.email())){
            throw new AlreadyExistException("Email already exists");
        }
        if (userRepository.existsByUsername(registerRequest.username())){
            throw new AlreadyExistException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRole(Roles.USER);
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest loginRequest){
        UserDetails userDetails = userService.loadUserByUsername(loginRequest.username());

        if(!passwordEncoder.matches(loginRequest.password(), userDetails.getPassword())){
            throw new InvalidCredentialException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken){

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtService.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateToken(userDetails);

        return new AuthResponse(newAccessToken, refreshToken);
    }

}