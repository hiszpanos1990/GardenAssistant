package com.example.gardenassistant.garden.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record GardenResponse(
        Long id, String name, String description,
        String location, LocalDateTime lastWateredDate, int maxPlants) implements Serializable {
}
