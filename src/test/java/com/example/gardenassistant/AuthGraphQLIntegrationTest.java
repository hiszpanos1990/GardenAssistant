package com.example.gardenassistant;

import com.example.gardenassistant.garden.entity.Garden;
import com.example.gardenassistant.garden.entity.Plant;
import com.example.gardenassistant.garden.entity.Roles;
import com.example.gardenassistant.garden.entity.User;
import com.example.gardenassistant.garden.repository.GardenRepository;
import com.example.gardenassistant.garden.repository.PlantRepository;
import com.example.gardenassistant.garden.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.AutoConfigureGraphQl;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.cache.type=none")
@AutoConfigureHttpGraphQlTester
@Transactional
public class AuthGraphQLIntegrationTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldLoginAndReceiveToken(){
        User user = new User();
        user.setUsername("testQL");
        user.setEmail("testQl@test.test");
        user.setRole(Roles.USER);
        user.setRefreshToken("");
        user.setPassword(passwordEncoder.encode("test123"));

        userRepository.save(user);

        String mutation = """
                    mutation {
                        login(input: {username: "testQL", password: "test123"})
                        {
                            accessToken
                            refreshToken
                        }
                    }
                """;

        graphQlTester.document(mutation).execute().path("login.accessToken").hasValue();
    }


    @Test
    void shouldAccessSecuredQueryWithJwtToken() {
        User user = new User();
        user.setUsername("test2");
        user.setEmail("test2@test.pl");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Roles.USER);
        user.setRefreshToken("");

        userRepository.save(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test2",
                        null,
                        List.of()
                )
        );

        String loginMutation = """
                mutation {
                    login(input: {
                        username: "test2",
                        password: "password"
                    }) {
                        accessToken
                        refreshToken
                    }
                }
                """;

        String token = graphQlTester.document(loginMutation)
                .execute()
                .path("login.accessToken")
                .entity(String.class)
                .get();

        String securedQuery = """
                query {
                    myGardens(page: 0, size: 10) {
                        totalElements
                    }
                }
                """;

        graphQlTester.mutate()
                .headers(headers -> headers.setBearerAuth(token))
                .build()
                .document(securedQuery)
                .execute()
                .path("myGardens.totalElements")
                .entity(Integer.class)
                .isEqualTo(0);


    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoAdminRole() {
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@test.pl");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Roles.USER);
        user.setRefreshToken("");

        userRepository.save(user);

        String loginMutation = """
                mutation {
                    login(input: {
                        username: "user",
                        password: "password"
                    }) {
                        accessToken
                        refreshToken
                    }
                }
                """;

        String token = graphQlTester.document(loginMutation)
                .execute()
                .path("login.accessToken")
                .entity(String.class)
                .get();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );


        String mutation = """
            mutation {
                removePlant(plantId: 1)
            }
            """;

        graphQlTester
                .mutate()
                .headers(headers -> headers.setBearerAuth(token))
                .build()
                .document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {

                    assertFalse(errors.isEmpty());

                    assertEquals(
                            "Access denied",
                            errors.get(0).getMessage()
                    );
                });

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldDeletePlantWhenUserHasAdminRole() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@test.pl");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Roles.ADMIN);
        user.setRefreshToken("");

        userRepository.save(user);

        Garden garden = new Garden();
        garden.setName("Garden");
        garden.setDescription("Desc");
        garden.setLocation("Warsaw");
        garden.setMaxPlants(10);
        garden.setUser(user);
        gardenRepository.save(garden);

        Plant plant = new Plant();
        plant.setName("Tomato");
        plant.setType("Vegetable");
        plant.setGarden(garden);
        plantRepository.save(plant);

        String loginMutation = """
                mutation {
                    login(input: {
                        username: "admin",
                        password: "password"
                    }) {
                        accessToken
                        refreshToken
                    }
                }
                """;

        String token = graphQlTester.document(loginMutation)
                .execute()
                .path("login.accessToken")
                .entity(String.class)
                .get();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );


        String mutation = """
            mutation {
                removePlant(plantId: %d)
            }
        """.formatted(plant.getId());

        graphQlTester
                .mutate()
                .headers(headers -> headers.setBearerAuth(token))
                .build()
                .document(mutation)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertTrue(errors.isEmpty());
                });
        assertFalse(plantRepository.existsById(plant.getId()));
        SecurityContextHolder.clearContext();
    }
}
