package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.exception.GardenFullException;
import com.example.gardenassistant.exception.GardenNotFoundException;
import com.example.gardenassistant.garden.dto.CreatePlantRequest;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.dto.PlantProfileResponse;
import com.example.gardenassistant.garden.dto.PlantResponse;
import com.example.gardenassistant.garden.entity.*;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.garden.repository.PlantProfileRepository;
import com.example.gardenassistant.garden.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
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
    private final PlantProfileRepository plantProfileRepository;


    @Transactional
    public PlantResponse addPlantToGarden(Long gardenId, CreatePlantRequest plantRequest){
        User currentUser = currentUserService.getCurrentUser();
        Garden garden = gardenRepository.findById(gardenId).orElseThrow(()-> new GardenNotFoundException("Garden not found"));
        if (currentUser.getId() != garden.getUser().getId()){
            throw new RuntimeException("You are not allowed to add plants to this garden");
        }

        long currentPlantsSize = plantRepository.countByGardenId(gardenId);
        if(currentPlantsSize >= garden.getMaxPlants()){
            throw new GardenFullException("Garden is full");
        }

        PlantProfile plantProfile = plantProfileRepository.findById(plantRequest.plantProfileId())
                .orElseThrow(()-> new GardenNotFoundException("Plant profile not found"));

        PlantProfileResponse plantProfileResponse =
                new PlantProfileResponse(plantProfile);


        Plant plant = new Plant();
        plant.setName(plantRequest.name());
        plant.setGarden(garden);
        plant.setProfile(plantProfile);
        plant.setPlantedDate(LocalDateTime.now());
        plant.setStatus(PlantStatus.HEALTHY);


        Plant savedPlant = plantRepository.save(plant);

        return new PlantResponse(savedPlant.getId(), savedPlant.getName(), plantProfileResponse, savedPlant.getStatus().name());
    }
    @Transactional(readOnly = true)
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
                                                new PlantProfileResponse(plant.getProfile()), plant.getStatus().name()),
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

    @Transactional
    public Boolean waterPlant(Long plantId) {
        User currentUser = currentUserService.getCurrentUser();
        Plant plant = plantRepository.findById(plantId).orElseThrow(() -> new GardenNotFoundException("Plant not found"));
        if (!plant.getGarden().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not allowed to water this plant");
        }
        plant.setLastWateredDate(LocalDateTime.now());
        return true;
    }
}
