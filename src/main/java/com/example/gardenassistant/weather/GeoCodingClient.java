package com.example.gardenassistant.weather;

import com.example.gardenassistant.garden.dto.Coordinates;
import com.example.gardenassistant.garden.dto.GeoLocationResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeoCodingClient {

    @Qualifier("geoCodingWebClient")
    private final WebClient webClient;

    GeoCodingClient(@Qualifier("geoCodingWebClient") WebClient webClient){
        this.webClient = webClient;
    }

    @Cacheable(value = "geoCoding" , key = "#location.toLowerCase()")
    @Retry(name = "geoCodingService" , fallbackMethod = "geoCodingFallback")
    @CircuitBreaker(name = "geoCodingService" , fallbackMethod = "geoCodingFallback")
    public GeoLocationResponse getCoordinatesByLocation(String location){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("v1/search")
                        .queryParam("name", location)
                        .queryParam("count", 1)
                        .queryParam("language", "en")
                        .queryParam("countryCode", "PL")
                        .queryParam("format", "json")
                        .build()
                ).retrieve()
                .bodyToMono(GeoLocationResponse.class)
                .block();
    }

    public GeoLocationResponse geoCodingFallback(String location, Exception exception){
        return new GeoLocationResponse(List.of(new Coordinates(-100,-100)));
    }

}
