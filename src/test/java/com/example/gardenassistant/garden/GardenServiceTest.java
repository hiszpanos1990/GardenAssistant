package com.example.gardenassistant.garden;

import com.example.gardenassistant.exception.UserNotFoundException;
import com.example.gardenassistant.garden.dto.CreateGardenRequest;
import com.example.gardenassistant.garden.dto.GardenPageResponse;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.garden.repository.UserRepository;
import com.example.gardenassistant.garden.service.CurrentUserService;
import com.example.gardenassistant.garden.service.GardenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GardenServiceTest {

    @Mock
    private GardenRepository gardenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GardenService gardenService;

    @Mock
    private CurrentUserService currentUserService;


    @Test
    void shouldCreateGarden(){
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test", null)
        );

        CreateGardenRequest gardenRequest = new CreateGardenRequest("Garden", "Desc",
                "Warsaw", 10);

        Garden savedGarden = new Garden();
        savedGarden.setId(1L);
        savedGarden.setName(gardenRequest.name());
        savedGarden.setDescription(gardenRequest.description());
        savedGarden.setLocation(gardenRequest.location());
        savedGarden.setMaxPlants(gardenRequest.maxPlants());
        savedGarden.setUser(user);

        when(gardenRepository.save(any (Garden.class))).thenReturn(savedGarden);

        // act
        GardenResponse response = gardenService.createGarden(gardenRequest);

        // assert
        assertEquals(1L, response.id());
        assertEquals("Garden", response.name());
        assertEquals("Desc", response.description());
        assertEquals("Warsaw", response.location());
        assertEquals(10, response.maxPlants());


        verify(gardenRepository).save(any(Garden.class));

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetMyAllGardens(){
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test", null)
        );

        Pageable pageable = PageRequest.of(0, 10);

        Garden garden = new Garden();
        garden.setId(1L);
        garden.setName("Garden");
        garden.setDescription("Desc");
        garden.setLocation("Warsaw");
        garden.setMaxPlants(10);
        garden.setUser(user);

        Page<Garden> pageableGardens = new PageImpl<>(List.of(garden), pageable, 1L);

        when(currentUserService.getCurrentUser()).thenReturn(user);
       when(gardenRepository.findAllByUserId(user.getId(), pageable)).thenReturn(pageableGardens);

        GardenPageResponse userGardens = gardenService.getUserGardens(user.getUsername(),pageable);

        assertEquals(1, userGardens.content().size());
        assertEquals("Garden", userGardens.content().get(0).name());
        assertEquals(1, userGardens.totalElements());

        verify(currentUserService).getCurrentUser();
        verify(gardenRepository).findAllByUserId(user.getId(), pageable);

        SecurityContextHolder.clearContext();

    }

    @Test
    void shouldThrowExceptionWhenUserNotFound(){
        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test", null)
        );

        when(currentUserService.getCurrentUser()).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class,()-> gardenService.createGarden(new CreateGardenRequest("Garden", "Desc",
                "Warsaw", 10)));

        verifyNoInteractions(gardenRepository);

        SecurityContextHolder.clearContext();
    }
}
