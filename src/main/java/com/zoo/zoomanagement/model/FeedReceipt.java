package com.zoo.zoomanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * Поступление корма на склад
 */
@Entity
@Table(name = "feed_receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "feed_type_id", nullable = false)
    private FeedType feedType;

    @Column(nullable = false)
    private Double quantity; // количество

    @Column(nullable = false)
    private LocalDate receiptDate; // дата поступления

    private String supplier; // поставщик

    private Double price; // цена за единицу

    private Double totalPrice; // общая сумма

    private String invoiceNumber; // номер накладной

    private String notes; // примечания
}
