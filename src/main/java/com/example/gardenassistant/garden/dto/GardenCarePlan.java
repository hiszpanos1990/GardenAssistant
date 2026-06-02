package com.example.gardenassistant.garden.dto;

import java.util.List;

public record GardenCarePlan(
        Long gardenId,
        String gardenName,
        String overallStatus,
        GardenRecommendation gardenRecommendation,
        List<PlantRecommendation> plantsRecommendation,
        int plantsCount,
        Long warningsCount,
        Long criticalCount
        ) {
}
