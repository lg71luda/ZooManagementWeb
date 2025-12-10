package com.zoo.zoomanagement.repository;

import com.zoo.zoomanagement.model.Feeding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedingRepository extends JpaRepository<Feeding, Long> {
}