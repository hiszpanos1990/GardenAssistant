package com.example.gardenassistant.garden.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "garden_activities")
public class GardenActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gardenId;
    private Long plantId;
    private String plantName;

    @Enumerated(EnumType.STRING)
    private GardenActivityType type;

    private String message;
    private LocalDateTime createdAt;
}
