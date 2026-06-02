package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.garden.dto.GardenActivityResponse;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.entity.GardenActivity;
import com.example.gardenassistant.garden.entity.GardenActivityType;
import com.example.gardenassistant.garden.repository.GardenActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GardenActivityService {
    private final GardenActivityRepository gardenActivityRepository;

    public void recordGardenWatered(Long gardenId) {
        GardenActivity gardenActivity = GardenActivity.builder()
                .gardenId(gardenId)
                .type(GardenActivityType.GARDEN_WATERED)
                .message("Podlano ogród")
                .createdAt(LocalDateTime.now()).build();

        gardenActivityRepository.save(gardenActivity);
    }

    public void recordPlantWatered(Long gardenId, Long plantId, String plantName) {

        GardenActivity gardenActivity = GardenActivity.builder()
                .plantId(plantId)
                .gardenId(gardenId)
                .plantName(plantName)
                .type(GardenActivityType.PLANT_WATERED)
                .message("Podlano roślinę "+plantName)
                .createdAt(LocalDateTime.now()).build();

        gardenActivityRepository.save(gardenActivity);
    }

    public List<GardenActivityResponse> getGardenActivities(Long gardenId){
        return gardenActivityRepository.findTop20ByGardenIdOrderByCreatedAt(gardenId).stream()
                .map(this::mapGardenActivities)
                .toList();
    }

    private GardenActivityResponse mapGardenActivities(GardenActivity gardenActivity){
        return new GardenActivityResponse(
                gardenActivity.getId(),
                gardenActivity.getGardenId(),
                gardenActivity.getPlantId(),
                gardenActivity.getPlantName(),
                gardenActivity.getType().name(),
                gardenActivity.getMessage(),
                gardenActivity.getCreatedAt()
        );
    }
}
