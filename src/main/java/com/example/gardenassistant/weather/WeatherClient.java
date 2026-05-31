package com.example.gardenassistant.weather;

import com.example.gardenassistant.garden.dto.CurrentWeather;
import com.example.gardenassistant.garden.dto.WeatherResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WeatherClient {
    private final WebClient webClient;

    @Retry(name = "weatherService", fallbackMethod = "fallbackWeather")
    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackWeather")
    public WeatherResponse getWeather(String latitude, String longitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current", "temperature_2m,wind_speed_10m,weather_code")
                        .build()
                )
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .block();
    }

    public WeatherResponse fallbackWeather(String latitude, String longitude, Exception exception) {

        CurrentWeather current = new CurrentWeather(
                -999,
                -999,
                -1
        );

        return new WeatherResponse(current);
    }
}
