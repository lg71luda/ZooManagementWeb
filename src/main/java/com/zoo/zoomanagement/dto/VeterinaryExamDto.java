package com.zoo.zoomanagement.dto;

import com.zoo.zoomanagement.model.VeterinaryExam;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VeterinaryExamDto {

    private Long id;

    @NotNull(message = "Выберите животное")
    private Long animalId;

    // Ветеринар устанавливается автоматически из аутентификации
    private Long veterinarianId;

    @NotNull(message = "Укажите дату осмотра")
    private LocalDateTime examDate;

    @Positive(message = "Вес должен быть положительным")
    private Double weight;

    @DecimalMin(value = "30.0", message = "Температура слишком низкая")
    @DecimalMax(value = "45.0", message = "Температура слишком высокая")
    private Double temperature;

    @Min(value = 20, message = "Пульс слишком низкий")
    @Max(value = 300, message = "Пульс слишком высокий")
    private Integer heartRate;

    @Min(value = 5, message = "Частота дыхания слишком низкая")
    @Max(value = 100, message = "Частота дыхания слишком высокая")
    private Integer respiratoryRate;

    @NotNull(message = "Выберите статус здоровья")
    private VeterinaryExam.HealthStatus healthStatus;

    @Size(max = 500, message = "Диагноз слишком длинный")
    private String diagnosis;

    @Size(max = 1000, message = "Описание лечения слишком длинное")
    private String treatment;

    @Size(max = 500, message = "Примечания слишком длинные")
    private String notes;

    private LocalDate nextExamDate;
}
