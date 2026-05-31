package com.example.gardenassistant.garden.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PlantScheduler {
    Logger logger = LoggerFactory.getLogger(PlantScheduler.class);

    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void scheduleWatering() {
        logger.info("Watering plants...");
    }
}
