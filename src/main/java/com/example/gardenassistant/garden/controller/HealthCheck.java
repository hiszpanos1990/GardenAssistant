package com.example.gardenassistant.garden.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HealthCheck {

    @QueryMapping
    public String healthCheck() {
        return "Healthy";
    }
}
