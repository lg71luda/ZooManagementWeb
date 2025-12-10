package com.zoo.zoomanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // Взрослый, Детский, Льготный, Семейный

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private LocalDateTime saleDate = LocalDateTime.now();

    private String cashierLogin;
}
