package com.zoo.zoomanagement.repository;

import com.zoo.zoomanagement.model.FeedReceipt;
import com.zoo.zoomanagement.model.FeedType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FeedReceiptRepository extends JpaRepository<FeedReceipt, Long> {

    List<FeedReceipt> findByFeedType(FeedType feedType);

    List<FeedReceipt> findByReceiptDateBetween(LocalDate start, LocalDate end);

    List<FeedReceipt> findByFeedTypeId(Long feedTypeId);
}
