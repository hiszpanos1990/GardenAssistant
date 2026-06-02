package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.garden.dto.*;
import com.example.gardenassistant.garden.entity.GardenActivity;
import com.example.gardenassistant.garden.entity.RecommendationLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GardenCarePlanService {
    private final GardenService gardenService;
    private final GardenRecommendationService gardenRecommendationService;
    private final PlantRecommendationService plantRecommendationService;
    private final GardenActivityService gardenActivityService;


    public GardenCarePlan getGardenCarePlan(Long gardenId){
        GardenResponse gardenById = gardenService.getGardenById(gardenId);

        GardenRecommendation gardenRecommendation = gardenRecommendationService.gardenRecommendation(gardenId);
        List<PlantRecommendation> plantRecommendation = plantRecommendationService.getPlantRecommendation(gardenId);

        long warningsCount = countBySeverity(plantRecommendation, RecommendationLevel.WARNING);
        long criticalCount = countBySeverity(plantRecommendation, RecommendationLevel.DANGER);

        String overallStatus = resolveOverallStatus(
                gardenRecommendation,
                warningsCount,
                criticalCount
        );

        List<GardenActivityResponse> recentActivities = gardenActivityService.getGardenActivities(gardenId);

        return new GardenCarePlan(gardenId,gardenById.name(),overallStatus,
                gardenById.lastWateredDate(),gardenRecommendation,plantRecommendation
        ,plantRecommendation.size(),warningsCount, criticalCount, recentActivities);
    }

    private long countBySeverity(List <PlantRecommendation> plantRecommendation, RecommendationLevel level){
        return plantRecommendation.stream()
                .filter(recommend -> recommend.severity().equals(level.name()))
                .count();
    }

    private String resolveOverallStatus(GardenRecommendation gardenRecommendation, long warningsCount, long criticalCount){
        if (criticalCount > 0){
            return "CRITICAL";
        }
        if (warningsCount > 0 || "WARNING".equals(gardenRecommendation.severity())) {
            return "WARNING";
        }
        return "OK";
    }
}
