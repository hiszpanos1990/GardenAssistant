package com.example.gardenassistant.garden.dto;

public record CreateGardenRequest(
        String name, String description,
        String location,int maxPlants) {
}
