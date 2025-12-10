package com.zoo.zoomanagement.repository;

import com.zoo.zoomanagement.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}