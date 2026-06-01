package com.example.gardenassistant.garden.service;

import com.example.gardenassistant.garden.dto.Coordinates;
import com.example.gardenassistant.garden.dto.CurrentWeather;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.dto.GeoLocationResponse;
import com.example.gardenassistant.weather.GeoCodingClient;
import com.example.gardenassistant.weather.WeatherClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GardenWeatherService {
    private final GeoCodingClient geoCodingClient;
    private final WeatherClient weatherClient;
    private final GardenService gardenService;

    public CurrentWeather getWeatherForGarden(Long gardenId){
        GardenResponse gardenById = gardenService.getGardenById(gardenId);
        GeoLocationResponse coordinatesByLocation = geoCodingClient.getCoordinatesByLocation(gardenById.location());
        Coordinates coordinates = coordinatesByLocation.results().getFirst();

        return weatherClient.getWeather(String.valueOf(coordinates.latitude()),
                String.valueOf(coordinates.longitude())).current();

    }
}
