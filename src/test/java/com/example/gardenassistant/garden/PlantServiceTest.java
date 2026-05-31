package com.example.gardenassistant.garden;

import com.example.gardenassistant.exception.GardenFullException;
import com.example.gardenassistant.exception.UserNotFoundException;
import com.example.gardenassistant.garden.dto.CreateGardenRequest;
import com.example.gardenassistant.garden.dto.CreatePlantRequest;
import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.Plant;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.garden.repository.PlantRepository;
import com.example.gardenassistant.garden.service.PlantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantServiceTest {

    @Mock
    private GardenRepository gardenRepository;

    @Mock
    private PlantRepository plantRepository;

    @InjectMocks
    private PlantService plantService;

    @Test
    void shouldNotAddPlantToGardenOverMaxPlants(){
        Garden garden = new Garden();
        garden.setMaxPlants(1);
        garden.setId(1L);
        garden.setName("Garden");
        garden.setDescription("Desc");
        garden.setLocation("Warsaw");

        when(gardenRepository.findById(1L)).thenReturn(Optional.of(garden));
        when(plantRepository.countByGardenId(1L)).thenReturn(2L);

        assertThrows(GardenFullException.class,()-> plantService.addPlantToGarden(1L, new CreatePlantRequest("test","test", "test")));

        verify(gardenRepository).findById(1L);

        verify(plantRepository).countByGardenId(1L);

        verify(plantRepository, never()).save(any());

    }

    @Test
    void shouldSavePlantWithCorrectData() {
        Garden garden = new Garden();
        garden.setMaxPlants(10);
        garden.setId(1L);
        garden.setName("Garden");
        garden.setDescription("Desc");
        garden.setLocation("Warsaw");

        CreatePlantRequest request =
                new CreatePlantRequest(
                        "Tomato",
                        "Vegetable",
                        "HEALTHY"
                );

        when(gardenRepository.findById(1L)).thenReturn(Optional.of(garden));
        when(plantRepository.countByGardenId(1L)).thenReturn(0L);

        when(plantRepository.save(any(Plant.class)))
                .thenAnswer(invocation -> {
                    Plant plant = invocation.getArgument(0);
                    plant.setId(1L);
                    return plant;
                });

        //act
        plantService.addPlantToGarden(1L,request);

        ArgumentCaptor<Plant> plantArgumentCaptor = ArgumentCaptor.forClass(Plant.class);

        verify(plantRepository).save(plantArgumentCaptor.capture());

        Plant savedPlant = plantArgumentCaptor.getValue();

        assertEquals("Tomato", savedPlant.getName());
        assertEquals("Vegetable", savedPlant.getType());
        assertEquals(garden, savedPlant.getGarden());
    }
}
