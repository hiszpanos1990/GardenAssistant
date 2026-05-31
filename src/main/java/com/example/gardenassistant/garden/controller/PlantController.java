package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.garden.dto.CreatePlantRequest;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.dto.PlantResponse;
import com.example.gardenassistant.garden.service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class PlantController {
    private final PlantService plantService;

    @MutationMapping
    public PlantResponse addPlant(@Argument Long gardenId, @Argument CreatePlantRequest plantRequest){
        return plantService.addPlantToGarden(gardenId, plantRequest);
    }

    @PreAuthorize( "hasRole('ADMIN')")
    @MutationMapping
    public boolean removePlant(@Argument Long plantId){
        return plantService.removePlantFromGarden(plantId);
    }

    @BatchMapping(typeName = "Garden", field = "plants")
    public Map<GardenResponse, List<PlantResponse>> plants(List<GardenResponse> gardens) {
        return plantService.getPlantsForGardens(gardens);
    }
}
