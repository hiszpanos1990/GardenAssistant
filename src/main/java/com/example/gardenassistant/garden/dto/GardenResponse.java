package com.example.gardenassistant.garden.dto;

import java.io.Serializable;

public record GardenResponse(
        Long id, String name, String description,
        String location, int maxPlants) implements Serializable {
}
