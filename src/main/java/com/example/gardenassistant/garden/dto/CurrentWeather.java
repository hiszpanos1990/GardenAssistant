package com.example.gardenassistant.garden.dto;

public record CurrentWeather( double temperature_2m,
                              double wind_speed_10m,
                              int weather_code) {
}