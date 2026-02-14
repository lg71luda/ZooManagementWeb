package com.zoo.zoomanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FeedReceiptDto {

    private Long id;

    @NotNull(message = "Выберите вид корма")
    private Long feedTypeId;

    @NotNull(message = "Укажите количество")
    @Positive(message = "Количество должно быть положительным")
    private Double quantity;

    @NotNull(message = "Укажите дату поступления")
    private LocalDate receiptDate;

    @Size(max = 100, message = "Название поставщика слишком длинное")
    private String supplier;

    @PositiveOrZero(message = "Цена не может быть отрицательной")
    private Double price;

    @Size(max = 50, message = "Номер накладной слишком длинный")
    private String invoiceNumber;

    @Size(max = 255, message = "Примечание слишком длинное")
    private String notes;
}
