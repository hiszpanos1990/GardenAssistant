package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.garden.dto.*;
import com.example.gardenassistant.garden.service.GardenService;
import com.example.gardenassistant.garden.service.ReportService;
import com.example.gardenassistant.weather.GeoCodingClient;
import com.example.gardenassistant.weather.WeatherClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Controller
public class GardenController {
    Logger log = LoggerFactory.getLogger(GardenController.class);
    private final GardenService gardenService;
    private final ReportService reportService;
    private final GeoCodingClient geoCodingClient;
    private final WeatherClient weatherClient;

    @QueryMapping
    public GardenPageResponse myGardens(@Argument int page, @Argument int size){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return gardenService.getUserGardens(userName,PageRequest.of(page, size));
    }

    @QueryMapping
    public CurrentWeather checkWeatherForGarden(@Argument Long gardenId){
        GardenResponse gardenById = gardenService.getGardenById(gardenId);
        GeoLocationResponse coordinatesByLocation = geoCodingClient.getCoordinatesByLocation(gardenById.location());
        Coordinates coordinates = coordinatesByLocation.results().getFirst();

        return weatherClient.getWeather(String.valueOf(coordinates.latitude()),
                String.valueOf(coordinates.longitude())).current();

    }

    @QueryMapping
    public GardenRecommendation gardenRecommendation(@Argument Long gardenId){
        return gardenService.gardenRecommendation(gardenId);
    }

    @MutationMapping
    public GardenResponse createGarden(@Argument CreateGardenRequest gardenRequest){
        return gardenService.createGarden(gardenRequest);
    }

    @MutationMapping
    public String generateGardenReport() {
        log.info(
                "Resolver thread: {}",
                Thread.currentThread().getName()
        );
        CompletableFuture<String> future =
                reportService.generateGardenReport();

        future.thenAccept(result -> log.info("Report generation result: {}", result));
        return "Report generation started";
    }
}
