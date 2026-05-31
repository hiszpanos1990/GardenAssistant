package com.example.gardenassistant.garden.repository;

import com.example.gardenassistant.garden.entity.Garden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GardenRepository extends JpaRepository<Garden, Long> {
    Page<Garden> findAllByUserId(Long userId, Pageable pageable);
    Optional<Garden> findByUserIdAndId(Long userId, Long id);

}
