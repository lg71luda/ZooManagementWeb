package com.zoo.zoomanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO для Ticket
 * Используется при продаже билетов
 */
@Data
public class TicketDto {

    private Long id;

    // Тип билета - обязательное поле
    @NotBlank(message = "Выберите тип билета")
    private String type;

    // Количество - обязательно, минимум 1
    @NotNull(message = "Укажите количество билетов")
    @Min(value = 1, message = "Количество билетов должно быть не менее 1")
    private Integer quantity;
}
