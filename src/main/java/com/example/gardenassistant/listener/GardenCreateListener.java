package com.example.gardenassistant.listener;

import com.example.gardenassistant.event.GardenCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GardenCreateListener {

    @Async
    @EventListener
    public void handleGardenCreated(GardenCreatedEvent event){

        log.info(
                "Garden created: {}",
                event.gardenName()
        );

        try {
            Thread.sleep(10000);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info(
                "Finished processing garden: {}",
                event.gardenName()
        );
    }
}
