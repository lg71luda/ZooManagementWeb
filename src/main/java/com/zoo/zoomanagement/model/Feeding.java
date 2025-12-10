package com.zoo.zoomanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Table(name = "feedings")
@Data
public class Feeding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false)
    private String feedType; // "Мясо", "Трава", "Рыба" и т.д.

    @Column(nullable = false)
    private Double quantity; // кг или литры

    @Column(nullable = false)
    private LocalTime time; // время кормления

    private String notes;
}
