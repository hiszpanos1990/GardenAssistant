package com.example.gardenassistant.garden.repository;

import com.example.gardenassistant.garden.entity.Plant;
import com.example.gardenassistant.garden.entity.PlantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    Page<Plant> findByGardenId(Long gardenId, Pageable pageable);
    List<Plant> findByStatus(PlantStatus status);
    long countByGardenId(Long gardenId);
    List<Plant> findByGardenId(Long gardenId);
    List<Plant> findByGardenIdIn(List<Long> gardenIds);
}
