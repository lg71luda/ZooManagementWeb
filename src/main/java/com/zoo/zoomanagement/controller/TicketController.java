package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.TicketDto;
import com.zoo.zoomanagement.model.Ticket;
import com.zoo.zoomanagement.repository.TicketRepository;
import jakarta.validation.Valid;
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
    public String stats(Model model) {
        List<Ticket> allTickets = ticketRepository.findAll();

        double totalRevenue = allTickets.stream().mapToDouble(Ticket::getTotalPrice).sum();
        long totalTickets = allTickets.size();
        double todayRevenue = allTickets.stream()
                .filter(t -> t.getSaleDate().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Ticket::getTotalPrice).sum();

        long adult = allTickets.stream().filter(t -> "Взрослый".equals(t.getType())).count();
        long child = allTickets.stream().filter(t -> "Детский".equals(t.getType())).count();
        long privileged = allTickets.stream().filter(t -> "Льготный".equals(t.getType())).count();
        long family = allTickets.stream().filter(t -> "Семейный".equals(t.getType())).count();

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("adult", adult);
        model.addAttribute("child", child);
        model.addAttribute("privileged", privileged);
        model.addAttribute("family", family);

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
