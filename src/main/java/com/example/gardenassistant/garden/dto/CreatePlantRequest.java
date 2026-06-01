package com.example.gardenassistant.garden.dto;

public record CreatePlantRequest(String name, Long plantProfileId, String status) {
}
