package com.example.gardenassistant.garden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "watering_schedules")
public class WateringSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int wateringInterval;
    private LocalDateTime nextWateringDate;
    private LocalDateTime lastWateringDate;
    private boolean isWatering;

    @OneToOne(mappedBy = "wateringSchedule")
    private Plant plant;
}
