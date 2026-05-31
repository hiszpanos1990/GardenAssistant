package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.event.GardenCreatedEvent;
import com.example.gardenassistant.garden.dto.*;
import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.entity.WeatherCondition;
import com.example.gardenassistant.garden.mapper.WeatherConditionMapper;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.weather.GeoCodingClient;
import com.example.gardenassistant.weather.WeatherClient;
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
    private final GeoCodingClient geoCodingClient;
    private final WeatherClient weatherClient;

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
        Garden garden = gardenRepository.findById(gardenId).orElseThrow();
        return new GardenResponse(
                garden.getId(),
                garden.getName(),
                garden.getDescription(),
                garden.getLocation(),
                garden.getMaxPlants()
        );
    }

    public GardenRecommendation gardenRecommendation(Long gardenId){
        GardenResponse gardenById = getGardenById(gardenId);
        GeoLocationResponse coordinatesByLocation = geoCodingClient.getCoordinatesByLocation(gardenById.location());
        Coordinates coordinates = coordinatesByLocation.results().getFirst();

        CurrentWeather current = weatherClient.getWeather(String.valueOf(coordinates.latitude()),
                String.valueOf(coordinates.longitude())).current();

        return getRecommendation(current);
    }

    private GardenRecommendation getRecommendation(CurrentWeather weather){
        WeatherCondition condition = WeatherConditionMapper.map(weather.weather_code());
        if (condition == WeatherCondition.RAIN
                || condition == WeatherCondition.RAIN_SHOWERS
                || condition == WeatherCondition.DRIZZLE) {
            return new GardenRecommendation(
                    "WATERING",
                    "INFO",
                    "Nie podlewaj ogrodu. Prognozowane są opady."
            );
        }
        if (weather.temperature_2m() >= 28) {
            return new GardenRecommendation(
                    "HEAT",
                    "WARNING",
                    "Jest gorąco. Podlej ogród wieczorem, nie w pełnym słońcu."
            );
        }

        if (weather.wind_speed_10m() >= 35) {
            return new GardenRecommendation(
                    "WIND",
                    "WARNING",
                    "Silny wiatr. Unikaj oprysków i zabezpiecz delikatne rośliny."
            );
        }
        log.info(condition.name());
        return new GardenRecommendation(
                "WATERING",
                "INFO",
                "Brak opadów. Sprawdź wilgotność gleby i rozważ podlewanie."
        );
    }
}
