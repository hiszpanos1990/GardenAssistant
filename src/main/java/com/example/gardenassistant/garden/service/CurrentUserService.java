package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.exception.UserNotFoundException;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepository;

    public String getCurrentUserName(){
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }

    public User getCurrentUser(){
        String userName = getCurrentUserName();
        return userRepository.findByUsername(userName)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
    }
}
