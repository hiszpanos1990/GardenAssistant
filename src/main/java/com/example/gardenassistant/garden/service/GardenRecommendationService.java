package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.garden.dto.*;
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
