package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.event.GardenCreatedEvent;
import com.example.gardenassistant.garden.dto.CreateGardenRequest;
import com.example.gardenassistant.garden.dto.GardenPageResponse;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.GardenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
