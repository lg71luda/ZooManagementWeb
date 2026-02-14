package com.zoo.zoomanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Осмотр животного ветеринаром
 */
@Entity
@Table(name = "veterinary_exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinaryExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Животное
    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    // Ветеринар (кто проводил осмотр)
    @ManyToOne
    @JoinColumn(name = "vet_id", nullable = false)
    private Staff veterinarian;

    // Дата и время осмотра
    @Column(nullable = false)
    private LocalDateTime examDate;

    // Физические показатели
    private Double weight; // вес в кг
    private Double temperature; // температура
    private Integer heartRate; // пульс
    private Integer respiratoryRate; // частота дыхания

    // Общее состояние
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus;

    // Диагноз
    @Column(length = 500)
    private String diagnosis;

    // Назначенное лечение
    @Column(length = 1000)
    private String treatment;

    // Примечания
    @Column(length = 500)
    private String notes;

    // Дата следующего осмотра
    private LocalDate nextExamDate;

    // Перечисление статусов здоровья
    public enum HealthStatus {
        HEALTHY("Здоров"),
        ATTENTION("Требует внимания"),
        SICK("Болеет"),
        RECOVERING("Выздоравливает"),
        QUARANTINE("Карантин");

        private final String displayName;

        HealthStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
