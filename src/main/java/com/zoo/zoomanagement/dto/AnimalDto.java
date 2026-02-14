package com.zoo.zoomanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) для Animal
 * Используется для валидации данных при создании/редактировании животного
 */
@Data
public class AnimalDto {

    private Long id;

    // Кличка животного - обязательное поле, от 2 до 50 символов
    @NotBlank(message = "Кличка животного обязательна")
    @Size(min = 2, max = 50, message = "Кличка должна быть от 2 до 50 символов")
    private String name;

    // Пол - не обязателен, но если указан - ограничение по длине
    @Size(max = 10, message = "Пол не может быть длиннее 10 символов")
    private String gender;

    // Дата рождения - не обязательна
    private LocalDate birthDate;

    // Дата прибытия - не обязательна
    private LocalDate arrivalDate;

    // Вид животного - обязательное поле
    @NotNull(message = "Выберите вид животного")
    private Long speciesId;

    // Вольер - не обязателен
    private Long enclosureId;

    // Имя файла фото - не обязательно
    private String photoUrl;
}
