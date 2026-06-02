package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.garden.dto.GardenActivityResponse;
import com.example.gardenassistant.garden.entity.GardenActivity;
import com.example.gardenassistant.garden.service.GardenActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GardenActivityController {
    private final GardenActivityService gardenActivityService;

    @QueryMapping
    public List<GardenActivityResponse> getGardenActivity(@Argument Long gardenId){
        return gardenActivityService.getGardenActivities(gardenId);
    }
}
