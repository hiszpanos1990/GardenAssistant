package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.event.GardenCreatedEvent;
import com.example.gardenassistant.exception.GardenNotFoundException;
import com.example.gardenassistant.garden.dto.CreateGardenRequest;
import com.example.gardenassistant.garden.dto.GardenPageResponse;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.dto.PlantRecommendation;
import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.Plant;
import com.example.gardenassistant.garden.entity.PlantProfile;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.GardenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GardenService {

    private final GardenRepository gardenRepository;
    private final CurrentUserService currentUserService;
    private final ApplicationEventPublisher publisher;

    @CacheEvict(value = "myGardens" , allEntries = true)
    @Transactional
    public GardenResponse createGarden(CreateGardenRequest gardenRequest) {
        User currentUser = currentUserService.getCurrentUser();

        Garden garden = new Garden();
        garden.setName(gardenRequest.name());
        garden.setDescription(gardenRequest.description());
        garden.setLocation(gardenRequest.location());
        garden.setMaxPlants(gardenRequest.maxPlants());
        garden.setUser(currentUser);

        Garden save = gardenRepository.save(garden);

        publisher.publishEvent(
                new GardenCreatedEvent(save.getId(), save.getName())
        );

        return new GardenResponse(save.getId(), save.getName(), save.getDescription(), save.getLocation(), save.getMaxPlants());
    }

    @Cacheable(
            value = "myGardens",
            key = "#userName + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public GardenPageResponse getUserGardens(String userName, Pageable pageable){
        User currentUser = currentUserService.getCurrentUser();
        Page<Garden> gardens = gardenRepository.findAllByUserId(currentUser.getId(), pageable);

        List<GardenResponse> content = gardens.stream()
                .map(garden -> new GardenResponse(garden.getId(),
                        garden.getName(), garden.getDescription(), garden.getLocation(), garden.getMaxPlants()))
                .toList();


        return new GardenPageResponse(
                content,
                gardens.getTotalElements(),
                gardens.getTotalPages(),
                gardens.getNumber()
        );
    }

    public GardenResponse getGardenById(Long gardenId){
        User currentUser = currentUserService.getCurrentUser();
        Garden garden = gardenRepository.findById(gardenId).orElseThrow();

        if(!garden.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not allowed to access this garden");
        }

        return new GardenResponse(
                garden.getId(),
                garden.getName(),
                garden.getDescription(),
                garden.getLocation(),
                garden.getMaxPlants()
        );
    }

    @Transactional
    public List<PlantRecommendation> getPlantRecommendations(Long gardenId) {
        Garden garden = gardenRepository.findById(gardenId).orElseThrow(() -> new GardenNotFoundException("Garden not found"));
        LocalDateTime now = LocalDateTime.now();

        return garden.getPlants().stream().map(plant -> recommendForPlant(plant, now))
                .toList();

    }

    private PlantRecommendation recommendForPlant(Plant plant, LocalDateTime now) {
        PlantProfile profile = plant.getProfile();
        LocalDateTime lastWatered = plant.getLastWateredDate();

        if (lastWatered == null){
            return new PlantRecommendation(
                    plant.getId(),
                    plant.getName(),
                    "WATERING",
                    "WARNING",
                    "Roślina nie była jeszcze podlewana. Podlej ją i zapisz podlewanie."
            );
        }

        long daysSinceWatering = ChronoUnit.DAYS.between(lastWatered, now);
        if (daysSinceWatering >= profile.getWateringFrequencyDays()){
            return new PlantRecommendation(
                    plant.getId(),
                    plant.getName(),
                    "WATERING",
                    "WARNING",
                    "Roślina wymaga podlewania. Ostatnie podlewanie było " + daysSinceWatering + " dni temu."
            );
        }


        return new PlantRecommendation(
                plant.getId(),
                plant.getName(),
                "WATERING",
                "INFO",
                "Podlewanie nie jest jeszcze wymagane."
        );
    }


}
