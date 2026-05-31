package com.example.gardenassistant.garden;

import com.example.gardenassistant.garden.dto.CreateGardenRequest;
import com.example.gardenassistant.garden.dto.GardenResponse;
import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.Roles;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.garden.repository.UserRepository;
import com.example.gardenassistant.garden.service.CurrentUserService;
import com.example.gardenassistant.garden.service.GardenService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class GardenServiceIntegrationTest {

    @Autowired
    private GardenService gardenService;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Spy
    private CurrentUserService currentUserService;

    @Test
    void shouldCreateGarden(){
        User user = new User();
        user.setUsername("test1");
        user.setEmail("test@test.pl");
        user.setPassword("password");
        user.setRole(Roles.USER);

        userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test1",
                        null
                )
        );

        CreateGardenRequest request =
                new CreateGardenRequest(
                        "Garden",
                        "Desc",
                        "Warsaw",
                        10
                );

        GardenResponse response =
                gardenService.createGarden(request);

        assertEquals("Garden",response.name());

        List<Garden> gardens = gardenRepository.findAll();

        assertEquals(1, gardens.size());

        Garden savedGarden =
                gardenRepository.findById(response.id()).orElseThrow();

        assertEquals("Garden", savedGarden.getName());
        assertEquals(user.getId(), savedGarden.getUser().getId() );


        SecurityContextHolder.clearContext();
    }
}
