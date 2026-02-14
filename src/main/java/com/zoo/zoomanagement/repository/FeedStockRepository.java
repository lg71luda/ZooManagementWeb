package com.zoo.zoomanagement.repository;

import com.zoo.zoomanagement.model.FeedStock;
import com.zoo.zoomanagement.model.FeedType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedStockRepository extends JpaRepository<FeedStock, Long> {

    Optional<FeedStock> findByFeedType(FeedType feedType);

    Optional<FeedStock> findByFeedTypeId(Long feedTypeId);

    List<FeedStock> findByQuantityLessThan(Double quantity);
}
