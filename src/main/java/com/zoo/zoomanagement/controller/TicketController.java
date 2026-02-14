package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.TicketDto;
import com.zoo.zoomanagement.model.Ticket;
import com.zoo.zoomanagement.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public String list(Model model, Authentication auth) {
        List<Ticket> allTickets = ticketRepository.findAll();
        model.addAttribute("tickets", allTickets);
        model.addAttribute("cashierName", auth.getName());

        double todayRevenue = allTickets.stream()
                .filter(t -> t.getSaleDate().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Ticket::getTotalPrice)
                .sum();
        model.addAttribute("todayRevenue", todayRevenue);

        return "tickets/list";
    }

    @GetMapping("/stats")
    public String stats(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        List<Ticket> allTickets = ticketRepository.findAll();

        // Определяем период по умолчанию (всё время)
        if (period == null && dateFrom == null && dateTo == null) {
            period = "all";
        }

        // Обрабатываем пресеты периодов
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.now();
        String periodLabel = "Всё время";

        if (period != null) {
            switch (period) {
                case "today":
                    startDate = LocalDate.now();
                    endDate = LocalDate.now();
                    periodLabel = "Сегодня";
                    break;
                case "yesterday":
                    startDate = LocalDate.now().minusDays(1);
                    endDate = LocalDate.now().minusDays(1);
                    periodLabel = "Вчера";
                    break;
                case "week":
                    startDate = LocalDate.now().minusWeeks(1);
                    periodLabel = "Последняя неделя";
                    break;
                case "month":
                    startDate = LocalDate.now().minusMonths(1);
                    periodLabel = "Последний месяц";
                    break;
                case "year":
                    startDate = LocalDate.now().minusYears(1);
                    periodLabel = "Последний год";
                    break;
                case "custom":
                    if (dateFrom != null) {
                        startDate = dateFrom;
                    }
                    if (dateTo != null) {
                        endDate = dateTo;
                    }
                    periodLabel = "Выбранный период";
                    break;
                case "all":
                default:
                    periodLabel = "Всё время";
                    break;
            }
        } else if (dateFrom != null || dateTo != null) {
            // Пользовательский период через dateFrom/dateTo
            startDate = dateFrom;
            if (dateTo != null) {
                endDate = dateTo;
            }
            periodLabel = "Выбранный период";
            period = "custom";
        }

        // Фильтруем билеты по периоду
        final LocalDate finalStartDate = startDate;
        final LocalDate finalEndDate = endDate;

        List<Ticket> filteredTickets = allTickets.stream()
                .filter(t -> {
                    LocalDate saleDate = t.getSaleDate().toLocalDate();
                    if (finalStartDate != null && saleDate.isBefore(finalStartDate)) {
                        return false;
                    }
                    if (finalEndDate != null && saleDate.isAfter(finalEndDate)) {
                        return false;
                    }
                    return true;
                })
                .toList();

        // Статистика за выбранный период
        double totalRevenue = filteredTickets.stream()
                .mapToDouble(Ticket::getTotalPrice)
                .sum();

        long totalTickets = filteredTickets.stream()
                .mapToLong(Ticket::getQuantity)
                .sum();

        long totalSales = filteredTickets.size();

        // Количество по типам
        long adult = filteredTickets.stream()
                .filter(t -> "Взрослый".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        long child = filteredTickets.stream()
                .filter(t -> "Детский".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        long privileged = filteredTickets.stream()
                .filter(t -> "Льготный".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        long family = filteredTickets.stream()
                .filter(t -> "Семейный".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        // Дополнительная статистика
        // Средний чек
        double averageCheck = totalSales > 0 ? totalRevenue / totalSales : 0;

        // Среднее количество билетов в продаже
        double avgTicketsPerSale = totalSales > 0 ? (double) totalTickets / totalSales : 0;

        // Передаём данные в модель
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("averageCheck", averageCheck);
        model.addAttribute("avgTicketsPerSale", avgTicketsPerSale);
        model.addAttribute("adult", adult);
        model.addAttribute("child", child);
        model.addAttribute("privileged", privileged);
        model.addAttribute("family", family);

        // Параметры периода
        model.addAttribute("period", period);
        model.addAttribute("periodLabel", periodLabel);
        model.addAttribute("dateFrom", startDate);
        model.addAttribute("dateTo", endDate);

        // Для сравнения: статистика за предыдущий период
        LocalDate prevStartDate = null;
        LocalDate prevEndDate = null;

        if (startDate != null && endDate != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            prevEndDate = startDate.minusDays(1);
            prevStartDate = prevEndDate.minusDays(days - 1);
        }

        if (prevStartDate != null) {
            final LocalDate finalPrevStartDate = prevStartDate;
            final LocalDate finalPrevEndDate = prevEndDate;

            double prevRevenue = allTickets.stream()
                    .filter(t -> {
                        LocalDate saleDate = t.getSaleDate().toLocalDate();
                        return !saleDate.isBefore(finalPrevStartDate) && !saleDate.isAfter(finalPrevEndDate);
                    })
                    .mapToDouble(Ticket::getTotalPrice)
                    .sum();

            // Процент изменения
            double revenueChange = prevRevenue > 0
                    ? ((totalRevenue - prevRevenue) / prevRevenue) * 100
                    : 0;

            model.addAttribute("revenueChange", revenueChange);
            model.addAttribute("prevRevenue", prevRevenue);
        } else {
            model.addAttribute("revenueChange", 0);
            model.addAttribute("prevRevenue", 0);
        }

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

        // Если есть ошибки валидации — возвращаем форму
        if (bindingResult.hasErrors()) {
            return "tickets/form";
        }

        // Создаём Ticket из DTO
        Ticket ticket = new Ticket();
        ticket.setType(ticketDto.getType());
        ticket.setQuantity(ticketDto.getQuantity());

        // Расчёт цены
        double pricePerTicket = switch (ticket.getType()) {
            case "Взрослый" -> 800.0;
            case "Детский" -> 400.0;
            case "Льготный" -> 500.0;
            case "Семейный" -> 2000.0;
            default -> 800.0;
        };
        ticket.setTotalPrice(pricePerTicket * ticket.getQuantity());
        ticket.setCashierLogin(auth.getName());

        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }
}
