package com.example.gardenassistant.garden.repository;

import com.example.gardenassistant.garden.entity.PlantProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantProfileRepository extends JpaRepository<PlantProfile, Long> {
}
