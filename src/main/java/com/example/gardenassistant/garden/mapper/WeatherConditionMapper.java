package com.example.gardenassistant.garden.mapper;

import com.example.gardenassistant.garden.entity.WeatherCondition;
import org.springframework.stereotype.Component;

public class WeatherConditionMapper {
    public static WeatherCondition map(int code) {
        return switch (code) {
            case 0 -> WeatherCondition.CLEAR;
            case 1, 2, 3 -> WeatherCondition.CLOUDY;
            case 45, 48 -> WeatherCondition.FOG;
            case 51, 53, 55 -> WeatherCondition.DRIZZLE;
            case 56, 57 -> WeatherCondition.FREEZING_DRIZZLE;
            case 61, 63, 65 -> WeatherCondition.RAIN;
            case 66, 67 -> WeatherCondition.FREEZING_RAIN;
            case 71, 73, 75, 77 -> WeatherCondition.SNOW;
            case 80, 81, 82 -> WeatherCondition.RAIN_SHOWERS;
            case 85, 86 -> WeatherCondition.SNOW_SHOWERS;
            case 95, 96, 99 -> WeatherCondition.THUNDERSTORM;
            default -> WeatherCondition.UNKNOWN;
        };
    }
}
