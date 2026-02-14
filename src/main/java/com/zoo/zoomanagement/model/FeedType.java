package com.zoo.zoomanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Вид корма
 */
@Entity
@Table(name = "feed_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Мясо, Рыба, Трава, Фрукты и т.д.

    @Column(nullable = false)
    private String unit = "кг"; // единица измерения

    @Column(nullable = false)
    private Double minStock = 10.0; // минимальный остаток для уведомления

    private String description; // описание
}
