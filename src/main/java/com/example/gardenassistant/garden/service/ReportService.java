package com.example.gardenassistant.garden.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    @Async("gardenExecutor")
    public CompletableFuture<String> generateGardenReport() {
        log.info("Start Generating garden report...");
        log.info(
                "Async thread: {}",
                Thread.currentThread().getName()
        );
        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Report generation interrupted", e);
        }
        log.info("End Generating garden report...");

        return CompletableFuture.completedFuture("Report generation completed");
    }
}
