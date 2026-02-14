package com.zoo.zoomanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Остатки корма на складе
 */
@Entity
@Table(name = "feed_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_type_id", nullable = false, unique = true)
    private FeedType feedType;

    @Column(nullable = false)
    private Double quantity = 0.0; // текущий остаток

    private LocalDateTime lastUpdated; // дата последнего обновления

    /**
     * Проверить, нужно ли пополнить запас
     */
    public boolean isLowStock() {
        return quantity < feedType.getMinStock();
    }
}
