package com.example.gardenassistant.garden.repository;

import com.example.gardenassistant.garden.entity.GardenActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardenActivityRepository extends JpaRepository<GardenActivity, Long> {
    List<GardenActivity> findTop20ByGardenIdOrderByCreatedAt(Long gardenId);
}
