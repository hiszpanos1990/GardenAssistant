package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.garden.dto.PlantRecommendation;
import com.example.gardenassistant.garden.service.PlantRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlantRecommendationController {

    private final PlantRecommendationService plantRecommendationService;
    
    @QueryMapping
    List<PlantRecommendation> getPlantRecommendation(@Argument Long gardenId){
        return plantRecommendationService.getPlantRecommendation(gardenId);
    }
}
