package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.TicketDto;
import com.zoo.zoomanagement.service.TicketService;
import com.zoo.zoomanagement.service.TicketService.TicketTypeStats;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public String list(Model model, Authentication auth) {
        var allTickets = ticketService.findAll();
        model.addAttribute("tickets", allTickets);
        model.addAttribute("cashierName", auth.getName());
        model.addAttribute("todayRevenue",
                ticketService.getRevenueForPeriod(LocalDate.now(), LocalDate.now()));
        return "tickets/list";
    }

    @GetMapping("/stats")
    public String stats(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        // Определяем период
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();
        String periodLabel = "Всё время";

        if (period == null && dateFrom == null && dateTo == null) {
            period = "all";
        }

        if (period != null) {
            switch (period) {
                case "today" -> { startDate = LocalDate.now(); periodLabel = "Сегодня"; }
                case "yesterday" -> { startDate = LocalDate.now().minusDays(1); endDate = LocalDate.now().minusDays(1); periodLabel = "Вчера"; }
                case "week" -> { startDate = LocalDate.now().minusWeeks(1); periodLabel = "Последняя неделя"; }
                case "month" -> { startDate = LocalDate.now().minusMonths(1); periodLabel = "Последний месяц"; }
                case "year" -> { startDate = LocalDate.now().minusYears(1); periodLabel = "Последний год"; }
                case "custom" -> {
                    startDate = dateFrom;
                    if (dateTo != null) endDate = dateTo;
                    periodLabel = "Выбранный период";
                }
                default -> periodLabel = "Всё время";
            }
        } else if (dateFrom != null || dateTo != null) {
            startDate = dateFrom;
            if (dateTo != null) endDate = dateTo;
            periodLabel = "Выбранный период";
            period = "custom";
        }

        // Получаем данные
        var tickets = ticketService.findAll();

        final LocalDate finalStart = startDate;
        final LocalDate finalEnd = endDate;

        var filteredTickets = tickets.stream()
                .filter(t -> {
                    LocalDate saleDate = t.getSaleDate().toLocalDate();
                    if (finalStart != null && saleDate.isBefore(finalStart)) return false;
                    if (finalEnd != null && saleDate.isAfter(finalEnd)) return false;
                    return true;
                })
                .toList();

        // Статистика
        double totalRevenue = filteredTickets.stream().mapToDouble(t -> t.getTotalPrice()).sum();
        long totalTickets = filteredTickets.stream().mapToLong(t -> t.getQuantity()).sum();
        long totalSales = filteredTickets.size();
        double averageCheck = totalSales > 0 ? totalRevenue / totalSales : 0;
        double avgTicketsPerSale = totalSales > 0 ? (double) totalTickets / totalSales : 0;

        TicketTypeStats typeStats = ticketService.getTypeStatsForPeriod(startDate, endDate);

        // Сравнение с прошлым периодом
        double revenueChange = 0;
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            LocalDate prevEnd = startDate.minusDays(1);
            LocalDate prevStart = prevEnd.minusDays(days - 1);
            double prevRevenue = ticketService.getRevenueForPeriod(prevStart, prevEnd);
            revenueChange = prevRevenue > 0 ? ((totalRevenue - prevRevenue) / prevRevenue) * 100 : 0;
        }

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("averageCheck", averageCheck);
        model.addAttribute("avgTicketsPerSale", avgTicketsPerSale);
        model.addAttribute("adult", typeStats.adult());
        model.addAttribute("child", typeStats.child());
        model.addAttribute("privileged", typeStats.privileged());
        model.addAttribute("family", typeStats.family());
        model.addAttribute("period", period);
        model.addAttribute("periodLabel", periodLabel);
        model.addAttribute("dateFrom", startDate);
        model.addAttribute("dateTo", endDate);
        model.addAttribute("revenueChange", revenueChange);

        return "tickets/stats";
    }

    @GetMapping("/sell")
    public String sellForm(Model model) {
        model.addAttribute("ticketDto", new TicketDto());
        return "tickets/form";
    }

    @PostMapping("/sell")
    public String sell(@Valid @ModelAttribute("ticketDto") TicketDto ticketDto,
                       BindingResult bindingResult,
                       Authentication auth) {

        if (bindingResult.hasErrors()) {
            return "tickets/form";
        }

        ticketService.sellTicket(ticketDto, auth.getName());
        return "redirect:/tickets";
    }
}
