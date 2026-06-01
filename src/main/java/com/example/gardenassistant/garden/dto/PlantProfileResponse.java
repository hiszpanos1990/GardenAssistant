package com.example.gardenassistant.garden.dto;

import com.example.gardenassistant.garden.entity.PlantProfile;

public record PlantProfileResponse(Long id, String name, Integer wateringFrequencyDays
        , Double minTemperature, Double maxTemperature, Boolean likesRain) {

    public PlantProfileResponse(PlantProfile plantProfile){
        this(plantProfile.getId(),plantProfile.getName(), plantProfile.getWateringFrequencyDays(),
                plantProfile.getMinTemperature(), plantProfile.getMaxTemperature(),
                plantProfile.getLikesRain());
    }
}
