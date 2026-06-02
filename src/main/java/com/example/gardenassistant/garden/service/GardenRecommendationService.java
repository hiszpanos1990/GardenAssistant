package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.garden.dto.*;
import com.example.gardenassistant.garden.entity.RecommendationLevel;
import com.example.gardenassistant.garden.entity.RecommendationType;
import com.example.gardenassistant.garden.entity.WeatherCondition;
import com.example.gardenassistant.garden.mapper.WeatherConditionMapper;
import com.example.gardenassistant.weather.GeoCodingClient;
import com.example.gardenassistant.weather.WeatherClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GardenRecommendationService {
    private final GeoCodingClient geoCodingClient;
    private final WeatherClient weatherClient;
    private final GardenService gardenService;

    public GardenRecommendation gardenRecommendation(Long gardenId){
        GardenResponse gardenById = gardenService.getGardenById(gardenId);
        GeoLocationResponse coordinatesByLocation = geoCodingClient.getCoordinatesByLocation(gardenById.location());
        if (coordinatesByLocation.results() == null || coordinatesByLocation.results().isEmpty()) {
            return new GardenRecommendation(
                    RecommendationType.LOCATION.name(),
                    RecommendationLevel.WARNING.name(),
                    "Nie udało się znaleźć współrzędnych dla lokalizacji ogrodu."
            );
        }

        Coordinates coordinates = coordinatesByLocation.results().getFirst();


        CurrentWeather current = weatherClient.getWeather(String.valueOf(coordinates.latitude()),
                String.valueOf(coordinates.longitude())).current();

        return getWeatherRecommendation(current);
    }

    private GardenRecommendation getWeatherRecommendation(CurrentWeather weather){
        WeatherCondition condition = WeatherConditionMapper.map(weather.weather_code());
        if (condition == WeatherCondition.RAIN
                || condition == WeatherCondition.RAIN_SHOWERS
                || condition == WeatherCondition.DRIZZLE) {
            return new GardenRecommendation(
                    RecommendationType.WATERING.name(),
                    RecommendationLevel.INFO.name(),
                    "Nie podlewaj ogrodu. Prognozowane są opady."
            );
        }
        if (weather.temperature_2m() >= 28) {
            return new GardenRecommendation(
                    RecommendationType.HEAT.name(),
                    RecommendationLevel.WARNING.name(),
                    "Jest gorąco. Podlej ogród wieczorem, nie w pełnym słońcu."
            );
        }

        if (weather.wind_speed_10m() >= 35) {
            return new GardenRecommendation(
                    RecommendationType.WIND.name(),
                    RecommendationLevel.WARNING.name(),
                    "Silny wiatr. Unikaj oprysków i zabezpiecz delikatne rośliny."
            );
        }
        log.info(condition.name());
        return new GardenRecommendation(
                RecommendationType.WATERING.name(),
                RecommendationLevel.INFO.name(),
                "Brak opadów. Sprawdź wilgotność gleby i rozważ podlewanie."
        );
    }
}
