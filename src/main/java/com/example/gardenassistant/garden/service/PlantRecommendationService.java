package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.garden.dto.PlantRecommendation;
import com.example.gardenassistant.garden.entity.Plant;
import com.example.gardenassistant.garden.entity.PlantProfile;
import com.example.gardenassistant.garden.entity.RecommendationLevel;
import com.example.gardenassistant.garden.entity.RecommendationType;
import com.example.gardenassistant.garden.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlantRecommendationService {
    private final PlantRepository plantRepository;

    @Transactional(readOnly = true)
    public List<PlantRecommendation> getPlantRecommendation(Long gardenId){
        LocalDateTime now = LocalDateTime.now();

        return plantRepository.findByGardenId(gardenId)
                .stream()
                .map(plant -> recommendForPlant(plant, now))
                .toList();
    }


    private PlantRecommendation recommendForPlant(Plant plant, LocalDateTime now) {
        PlantProfile profile = plant.getProfile();

        if (profile == null || profile.getWateringFrequencyDays() == null) {
            return new PlantRecommendation(plant.getId(), plant.getName(),
                    RecommendationType.WATERING.name(), RecommendationLevel.INFO.name(),
                    "Brak danych o częstotliwości podlewania dla tej rośliny.");
        }

        if (plant.getLastWateredDate() == null) {
            return new PlantRecommendation(plant.getId(), plant.getName(),
                    RecommendationType.WATERING.name(), RecommendationLevel.WARNING.name(),
                    "Roślina nie była jeszcze podlewana. Warto ją podlać.");
        }

        long daysSinceLastWatered = Duration.between(plant.getLastWateredDate(), now).toDays();
        int wateringFrequencyDays = profile.getWateringFrequencyDays();

        if (daysSinceLastWatered > wateringFrequencyDays) {
            long l = daysSinceLastWatered - wateringFrequencyDays;
            return new PlantRecommendation(plant.getId(), plant.getName(),
                    RecommendationType.WATERING.name(), RecommendationLevel.WARNING.name(),
                    "Roślina ma opóźnione podlewanie o " + daysSinceLastWatered + " dni.");
        }
        if (daysSinceLastWatered == wateringFrequencyDays) {
            return new PlantRecommendation(
                    plant.getId(),
                    plant.getName(),
                    RecommendationType.WATERING.name(),
                    RecommendationLevel.WARNING.name(),
                    "Roślina powinna zostać podlana dzisiaj."
            );
        }

        if (daysSinceLastWatered == wateringFrequencyDays - 1) {
            return new PlantRecommendation(
                    plant.getId(),
                    plant.getName(),
                    RecommendationType.WATERING.name(),
                    RecommendationLevel.INFO.name(),
                    "Roślina będzie wymagać podlewania wkrótce."
            );
        }

        long daysUntilNextWatering = wateringFrequencyDays - daysSinceLastWatered;

        return new PlantRecommendation(
                plant.getId(),
                plant.getName(),
                RecommendationType.WATERING.name(),
                RecommendationLevel.OK.name(),
                "Roślina nie wymaga jeszcze podlewania. Następne podlewanie za "
                        + daysUntilNextWatering + " dni."
        );

    }
}
