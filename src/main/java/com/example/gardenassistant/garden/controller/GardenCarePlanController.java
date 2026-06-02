package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.garden.dto.GardenCarePlan;
import com.example.gardenassistant.garden.service.GardenCarePlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GardenCarePlanController {
    private final GardenCarePlanService gardenCarePlanService;

    @QueryMapping
    public GardenCarePlan getGardenCarePlan(@Argument Long gardenId){
        return gardenCarePlanService.getGardenCarePlan(gardenId);
    }
}
