package com.zoo.zoomanagement.repository;

import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.model.VeterinaryExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VeterinaryExamRepository extends JpaRepository<VeterinaryExam, Long> {

    // Все осмотры животного
    List<VeterinaryExam> findByAnimalOrderByExamDateDesc(Animal animal);

    List<VeterinaryExam> findByAnimalIdOrderByExamDateDesc(Long animalId);

    // Осмотры ветеринаром
    List<VeterinaryExam> findByVeterinarianOrderByExamDateDesc(Staff veterinarian);

    List<VeterinaryExam> findByVeterinarianIdOrderByExamDateDesc(Long vetId);

    // Осмотры за период
    List<VeterinaryExam> findByExamDateBetween(LocalDateTime start, LocalDateTime end);

    // Предстоящие осмотры (nextExamDate >= сегодня)
    List<VeterinaryExam> findByNextExamDateGreaterThanEqualOrderByNextExamDateAsc(LocalDate date);

    // Просроченные осмотры (nextExamDate < сегодня)
    List<VeterinaryExam> findByNextExamDateLessThanOrderByNextExamDateAsc(LocalDate date);

    // По статусу здоровья
    List<VeterinaryExam> findByHealthStatus(VeterinaryExam.HealthStatus status);

    // Последний осмотр животного
    VeterinaryExam findFirstByAnimalIdOrderByExamDateDesc(Long animalId);
}
