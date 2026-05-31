package com.example.gardenassistant.garden;

import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.Roles;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.garden.repository.PlantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = "spring.cache.type=none")
@Import(GardenRepositoryTest.CacheTestConfig.class)
public class GardenRepositoryTest {

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @TestConfiguration
    static class CacheTestConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Test
    void shouldFindGardenByUserId(){
        User user = new User();
        user.setUsername("test");
        user.setEmail("test@test.pl");
        user.setPassword("password");
        user.setRole(Roles.USER);

        entityManager.persist(user);
        entityManager.flush();

        Garden garden = new Garden();
        garden.setName("Garden");
        garden.setDescription("Desc");
        garden.setLocation("Warsaw");
        garden.setMaxPlants(10);
        garden.setUser(user);
        entityManager.persist(garden);
        entityManager.flush();

        Page<Garden> result = gardenRepository.findAllByUserId(user.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Garden", result.getContent().get(0).getName());
    }
}
