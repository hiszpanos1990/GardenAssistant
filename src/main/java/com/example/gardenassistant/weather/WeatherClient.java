package com.example.gardenassistant.weather;

import com.example.gardenassistant.garden.dto.CurrentWeather;
import com.example.gardenassistant.garden.dto.WeatherResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class WeatherClient {
    @Qualifier("weatherWebClient")
    private final WebClient webClient;

    public WeatherClient(@Qualifier("weatherWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    @Cacheable(value = "weather" , key = "#latitude +':'+ #longitude")
    @Retry(name = "weatherService", fallbackMethod = "fallbackWeather")
    @CircuitBreaker(name = "weatherService", fallbackMethod = "fallbackWeather")
    public WeatherResponse getWeather(String latitude, String longitude) {
        log.info("Calling OpenMeteo API...");
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
