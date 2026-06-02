package com.example.gardenassistant.garden.dto;

import java.time.LocalDateTime;

public record GardenActivityResponse(
        Long id,
        Long gardenId,
        Long plantId,
        String plantName,
        String type,
        String message,
        LocalDateTime createdAt
) {
}
