package com.zoo.zoomanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FeedTypeDto {

    private Long id;

    @NotBlank(message = "Название вида корма обязательно")
    @Size(min = 2, max = 50, message = "Название должно быть от 2 до 50 символов")
    private String name;

    @NotBlank(message = "Укажите единицу измерения")
    private String unit = "кг";

    @Positive(message = "Минимальный остаток должен быть положительным числом")
    private Double minStock = 10.0;

    @Size(max = 255, message = "Описание слишком длинное")
    private String description;
}
