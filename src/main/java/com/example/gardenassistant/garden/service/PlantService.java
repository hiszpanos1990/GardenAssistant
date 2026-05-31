package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.exception.GardenFullException;
import com.example.gardenassistant.exception.GardenNotFoundException;
import com.example.gardenassistant.garden.dto.CreatePlantRequest;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.dto.PlantResponse;
import com.example.gardenassistant.garden.entity.*;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.garden.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlantService {
    private final GardenRepository gardenRepository;
    private final PlantRepository plantRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public PlantResponse addPlantToGarden(Long gardenId, CreatePlantRequest plantRequest){
        Garden garden = gardenRepository.findById(gardenId).orElseThrow(()-> new GardenNotFoundException("Garden not found"));
        long currentPlantsSize = plantRepository.countByGardenId(gardenId);
        if(currentPlantsSize >= garden.getMaxPlants()){
            throw new GardenFullException("Garden is full");
        }
        Plant plant = new Plant();
        plant.setName(plantRequest.name());
        plant.setGarden(garden);
        plant.setType(plantRequest.type());
        plant.setPlantedDate(LocalDateTime.now());
        plant.setStatus(PlantStatus.HEALTHY);


        Plant savedPlant = plantRepository.save(plant);

        return new PlantResponse(savedPlant.getId(), savedPlant.getName(), savedPlant.getType(), savedPlant.getStatus().name());
    }

    public Map<GardenResponse, List<PlantResponse>> getPlantsForGardens(List<GardenResponse> gardens) {
        List<Long> gardenIds = gardens.stream()
                .map(GardenResponse::id)
                .toList();

        List<Plant> plants = plantRepository.findByGardenIdIn(gardenIds);

        Map<Long, List<PlantResponse>> collect = plants.stream()
                .collect(Collectors
                        .groupingBy(plant -> plant.getGarden().getId(),
                                Collectors.mapping(plant ->
                                        new PlantResponse(plant.getId(), plant.getName(),
                                                plant.getType(), plant.getStatus().name()),
                                        Collectors.toList())
        ));

        return gardens.stream()
                .collect(Collectors
                        .toMap(garden -> garden,
                garden -> collect.getOrDefault(garden.id(), List.of())));
    }

    @Transactional
    public boolean removePlantFromGarden(Long plantId){
        User currentUser = currentUserService.getCurrentUser();
        Plant plant = plantRepository.findById(plantId).orElseThrow(() -> new GardenNotFoundException("Plant not found"));

        boolean isOwner = plant.getGarden()
                        .getUser().getId().equals(currentUser.getId());

        boolean isAdmin = currentUser.getRole().equals(Roles.ADMIN);

        if(!isOwner && !isAdmin){
            throw new RuntimeException("You are not allowed to delete this plant");
        }

        plantRepository.deleteById(plantId);
        return true;
    }
}
