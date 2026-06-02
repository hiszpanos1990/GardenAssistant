package com.example.gardenassistant.garden.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GardenCarePlan(
        Long gardenId,
        String gardenName,
        String overallStatus,
        LocalDateTime gardenLastWateredDate,
        GardenRecommendation gardenRecommendation,
        List<PlantRecommendation> plantsRecommendation,
        int plantsCount,
        Long warningsCount,
        Long criticalCount,
        List<GardenActivityResponse> recentActivities
        ) {
}
