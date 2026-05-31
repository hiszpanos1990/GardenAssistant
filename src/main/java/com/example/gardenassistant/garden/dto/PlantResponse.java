package com.example.gardenassistant.garden.dto;

import java.io.Serializable;

public record PlantResponse(Long id, String name, String type, String status) implements Serializable {
}
