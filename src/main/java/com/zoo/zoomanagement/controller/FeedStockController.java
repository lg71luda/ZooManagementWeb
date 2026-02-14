package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.FeedReceiptDto;
import com.zoo.zoomanagement.dto.FeedTypeDto;
import com.zoo.zoomanagement.model.FeedReceipt;
import com.zoo.zoomanagement.model.FeedStock;
import com.zoo.zoomanagement.model.FeedType;
import com.zoo.zoomanagement.service.FeedStockService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/feedstock")
public class FeedStockController {

    private final FeedStockService feedStockService;

    public FeedStockController(FeedStockService feedStockService) {
        this.feedStockService = feedStockService;
    }

    // ========== Склад (остатки) ==========

    @GetMapping
    public String stockList(Model model) {
        List<FeedStock> stocks = feedStockService.findAllStock();
        List<FeedStock> lowStock = feedStockService.findLowStock();

        model.addAttribute("stocks", stocks);
        model.addAttribute("lowStock", lowStock);
        model.addAttribute("lowStockCount", lowStock.size());
        model.addAttribute("totalQuantity", feedStockService.getTotalStockValue());

        return "feedstock/list";
    }

    // ========== Виды корма ==========

    @GetMapping("/types")
    public String feedTypesList(Model model) {
        model.addAttribute("feedTypes", feedStockService.findAllFeedTypes());
        return "feedstock/types";
    }

    @GetMapping("/types/new")
    public String newFeedTypeForm(Model model) {
        model.addAttribute("feedTypeDto", new FeedTypeDto());
        return "feedstock/type-form";
    }

    @PostMapping("/types")
    public String saveFeedType(@Valid @ModelAttribute("feedTypeDto") FeedTypeDto dto,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "feedstock/type-form";
        }

        if (dto.getId() != null) {
            feedStockService.updateFeedType(dto.getId(), dto);
        } else {
            feedStockService.createFeedType(dto);
        }

        return "redirect:/feedstock/types";
    }

    @GetMapping("/types/edit/{id}")
    public String editFeedTypeForm(@PathVariable Long id, Model model) {
        FeedType feedType = feedStockService.findFeedTypeById(id)
                .orElseThrow(() -> new IllegalArgumentException("Вид корма не найден"));

        FeedTypeDto dto = new FeedTypeDto();
        dto.setId(feedType.getId());
        dto.setName(feedType.getName());
        dto.setUnit(feedType.getUnit());
        dto.setMinStock(feedType.getMinStock());
        dto.setDescription(feedType.getDescription());

        model.addAttribute("feedTypeDto", dto);
        return "feedstock/type-form";
    }

    @PostMapping("/types/delete/{id}")
    public String deleteFeedType(@PathVariable Long id) {
        try {
            feedStockService.deleteFeedType(id);
        } catch (IllegalArgumentException e) {
            // Можно добавить сообщение об ошибке
        }
        return "redirect:/feedstock/types";
    }

    // ========== Поступления ==========

    @GetMapping("/receipts")
    public String receiptsList(
            @RequestParam(required = false) Long feedTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        List<FeedReceipt> receipts;

        if (feedTypeId != null) {
            receipts = feedStockService.findReceiptsByFeedType(feedTypeId);
        } else if (dateFrom != null && dateTo != null) {
            receipts = feedStockService.findReceiptsByPeriod(dateFrom, dateTo);
        } else {
            receipts = feedStockService.findAllReceipts();
        }

        model.addAttribute("receipts", receipts);
        model.addAttribute("feedTypes", feedStockService.findAllFeedTypes());
        model.addAttribute("selectedFeedTypeId", feedTypeId);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        return "feedstock/receipts";
    }

    @GetMapping("/receipts/new")
    public String newReceiptForm(Model model) {
        model.addAttribute("receiptDto", new FeedReceiptDto());
        model.addAttribute("feedTypes", feedStockService.findAllFeedTypes());
        return "feedstock/receipt-form";
    }

    @PostMapping("/receipts")
    public String saveReceipt(@Valid @ModelAttribute("receiptDto") FeedReceiptDto dto,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("feedTypes", feedStockService.findAllFeedTypes());
            return "feedstock/receipt-form";
        }

        feedStockService.createReceipt(dto);
        return "redirect:/feedstock/receipts";
    }
}
