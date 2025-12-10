package com.zoo.zoomanagement.repository;

import com.zoo.zoomanagement.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeciesRepository extends JpaRepository<Species, Long> {
}