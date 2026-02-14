package com.zoo.zoomanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalTime;

/**
 * DTO для Feeding
 * Используется при создании расписания кормления
 */
@Data
public class FeedingDto {

    private Long id;

    // Животное - обязательно
    @NotNull(message = "Выберите животное")
    private Long animalId;

    // Тип корма - обязательное поле
    @NotBlank(message = "Укажите тип корма")
    @Size(min = 2, max = 50, message = "Тип корма должен быть от 2 до 50 символов")
    private String feedType;

    // Количество - обязательно, положительное число
    @NotNull(message = "Укажите количество корма")
    @Positive(message = "Количество должно быть положительным числом")
    private Double quantity;

    // Время кормления - обязательно
    @NotNull(message = "Укажите время кормления")
    private LocalTime time;

    // Примечания - не обязательны
    @Size(max = 255, message = "Примечания не могут быть длиннее 255 символов")
    private String notes;
}
