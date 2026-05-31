package com.example.gardenassistant.garden.repository;

import com.example.gardenassistant.garden.entity.WateringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WateringScheduleRepository extends JpaRepository<WateringSchedule, Long> {
}
