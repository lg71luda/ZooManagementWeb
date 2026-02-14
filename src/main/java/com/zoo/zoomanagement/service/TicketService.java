package com.zoo.zoomanagement.service;

import com.zoo.zoomanagement.dto.TicketDto;
import com.zoo.zoomanagement.model.Ticket;
import com.zoo.zoomanagement.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для работы с билетами
 */
@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    // Цены на билеты
    private static final double PRICE_ADULT = 800.0;
    private static final double PRICE_CHILD = 400.0;
    private static final double PRICE_PRIVILEGED = 500.0;
    private static final double PRICE_FAMILY = 2000.0;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Получить все билеты
     */
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    /**
     * Продать билет (создать из DTO)
     */
    public Ticket sellTicket(TicketDto dto, String cashierLogin) {
        Ticket ticket = new Ticket();
        ticket.setType(dto.getType());
        ticket.setQuantity(dto.getQuantity());
        ticket.setTotalPrice(calculatePrice(dto.getType(), dto.getQuantity()));
        ticket.setCashierLogin(cashierLogin);

        return ticketRepository.save(ticket);
    }

    /**
     * Рассчитать стоимость билетов
     */
    public double calculatePrice(String type, int quantity) {
        double pricePerTicket = getPricePerTicket(type);
        return pricePerTicket * quantity;
    }

    /**
     * Получить цену за билет по типу
     */
    public double getPricePerTicket(String type) {
        return switch (type) {
            case "Взрослый" -> PRICE_ADULT;
            case "Детский" -> PRICE_CHILD;
            case "Льготный" -> PRICE_PRIVILEGED;
            case "Семейный" -> PRICE_FAMILY;
            default -> PRICE_ADULT;
        };
    }

    /**
     * Получить общую выручку
     */
    public double getTotalRevenue() {
        return ticketRepository.findAll().stream()
                .mapToDouble(Ticket::getTotalPrice)
                .sum();
    }

    /**
     * Получить выручку за период
     */
    public double getRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        return ticketRepository.findAll().stream()
                .filter(t -> {
                    LocalDate saleDate = t.getSaleDate().toLocalDate();
                    return !saleDate.isBefore(startDate) && !saleDate.isAfter(endDate);
                })
                .mapToDouble(Ticket::getTotalPrice)
                .sum();
    }

    /**
     * Получить общее количество проданных билетов
     */
    public long getTotalTicketsSold() {
        return ticketRepository.findAll().stream()
                .mapToLong(Ticket::getQuantity)
                .sum();
    }

    /**
     * Получить количество билетов за период
     */
    public long getTicketsSoldForPeriod(LocalDate startDate, LocalDate endDate) {
        return ticketRepository.findAll().stream()
                .filter(t -> {
                    LocalDate saleDate = t.getSaleDate().toLocalDate();
                    return !saleDate.isBefore(startDate) && !saleDate.isAfter(endDate);
                })
                .mapToLong(Ticket::getQuantity)
                .sum();
    }

    /**
     * Получить статистику по типам билетов за период
     */
    public TicketTypeStats getTypeStatsForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketRepository.findAll().stream()
                .filter(t -> {
                    LocalDate saleDate = t.getSaleDate().toLocalDate();
                    if (startDate != null && saleDate.isBefore(startDate)) {
                        return false;
                    }
                    if (endDate != null && saleDate.isAfter(endDate)) {
                        return false;
                    }
                    return true;
                })
                .toList();

        long adult = tickets.stream()
                .filter(t -> "Взрослый".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        long child = tickets.stream()
                .filter(t -> "Детский".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        long privileged = tickets.stream()
                .filter(t -> "Льготный".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        long family = tickets.stream()
                .filter(t -> "Семейный".equals(t.getType()))
                .mapToLong(Ticket::getQuantity)
                .sum();

        return new TicketTypeStats(adult, child, privileged, family);
    }

    /**
     * Вспомогательный класс для статистики по типам
     */
    public record TicketTypeStats(long adult, long child, long privileged, long family) {}
}
