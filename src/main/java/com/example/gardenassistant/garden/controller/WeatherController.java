package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.garden.dto.CurrentWeather;
import com.example.gardenassistant.garden.dto.WeatherResponse;
import com.example.gardenassistant.weather.WeatherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherClient weatherClient;

    @QueryMapping
    public CurrentWeather weather(@Argument String latitude, @Argument String longitude){
        return weatherClient.getWeather(latitude, longitude).current();
    }
}
