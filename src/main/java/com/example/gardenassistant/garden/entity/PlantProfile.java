package com.example.gardenassistant.garden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "plant_profiles")
public class PlantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private Integer wateringFrequencyDays;
    private Double minTemperature;
    private Double maxTemperature;
    private Boolean likesRain;
}
