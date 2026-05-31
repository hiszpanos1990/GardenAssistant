package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.garden.dto.AuthResponse;
import com.example.gardenassistant.garden.dto.LoginRequest;
import com.example.gardenassistant.garden.dto.RegisterRequest;
import com.example.gardenassistant.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final AuthService authService;

    @MutationMapping
    public AuthResponse register(@Argument @Valid RegisterRequest input){
        return authService.register(input);
    }

    @MutationMapping
    public AuthResponse login(@Argument LoginRequest input){
        return authService.login(input);
    }

    @MutationMapping
    public AuthResponse refreshToken(@Argument String refreshToken){
        return authService.refreshToken(refreshToken);
    }

}
