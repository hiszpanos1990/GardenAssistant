package com.example.gardenassistant.garden.dto;

public record PlantRecommendation(Long plantId, String plantName, String type,
                                  String severity, String message) {
}
