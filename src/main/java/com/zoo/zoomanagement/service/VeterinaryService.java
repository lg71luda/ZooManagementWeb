package com.zoo.zoomanagement.service;

import com.zoo.zoomanagement.dto.VeterinaryExamDto;
import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.model.VeterinaryExam;
import com.zoo.zoomanagement.repository.AnimalRepository;
import com.zoo.zoomanagement.repository.StaffRepository;
import com.zoo.zoomanagement.repository.VeterinaryExamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с ветеринарными осмотрами
 */
@Service
public class VeterinaryService {

    private final VeterinaryExamRepository examRepository;
    private final AnimalRepository animalRepository;
    private final StaffRepository staffRepository;

    public VeterinaryService(VeterinaryExamRepository examRepository,
                             AnimalRepository animalRepository,
                             StaffRepository staffRepository) {
        this.examRepository = examRepository;
        this.animalRepository = animalRepository;
        this.staffRepository = staffRepository;
    }

    // ========== Осмотры ==========

    public List<VeterinaryExam> findAll() {
        return examRepository.findAll();
    }

    public Optional<VeterinaryExam> findById(Long id) {
        return examRepository.findById(id);
    }

    public List<VeterinaryExam> findByAnimalId(Long animalId) {
        return examRepository.findByAnimalIdOrderByExamDateDesc(animalId);
    }

    public List<VeterinaryExam> findByVeterinarianId(Long vetId) {
        return examRepository.findByVeterinarianIdOrderByExamDateDesc(vetId);
    }

    /**
     * Создать осмотр из DTO
     */
    public VeterinaryExam createFromDto(VeterinaryExamDto dto, String vetLogin) {
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new IllegalArgumentException("Животное не найдено: " + dto.getAnimalId()));

        Staff veterinarian = staffRepository.findByLogin(vetLogin)
                .orElseThrow(() -> new IllegalArgumentException("Ветеринар не найден: " + vetLogin));

        VeterinaryExam exam = new VeterinaryExam();
        exam.setAnimal(animal);
        exam.setVeterinarian(veterinarian);
        exam.setExamDate(dto.getExamDate() != null ? dto.getExamDate() : LocalDateTime.now());
        exam.setWeight(dto.getWeight());
        exam.setTemperature(dto.getTemperature());
        exam.setHeartRate(dto.getHeartRate());
        exam.setRespiratoryRate(dto.getRespiratoryRate());
        exam.setHealthStatus(dto.getHealthStatus());
        exam.setDiagnosis(dto.getDiagnosis());
        exam.setTreatment(dto.getTreatment());
        exam.setNotes(dto.getNotes());
        exam.setNextExamDate(dto.getNextExamDate());

        return examRepository.save(exam);
    }

    /**
     * Обновить осмотр
     */
    public VeterinaryExam updateFromDto(Long id, VeterinaryExamDto dto) {
        VeterinaryExam exam = examRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Осмотр не найден: " + id));

        exam.setExamDate(dto.getExamDate());
        exam.setWeight(dto.getWeight());
        exam.setTemperature(dto.getTemperature());
        exam.setHeartRate(dto.getHeartRate());
        exam.setRespiratoryRate(dto.getRespiratoryRate());
        exam.setHealthStatus(dto.getHealthStatus());
        exam.setDiagnosis(dto.getDiagnosis());
        exam.setTreatment(dto.getTreatment());
        exam.setNotes(dto.getNotes());
        exam.setNextExamDate(dto.getNextExamDate());

        return examRepository.save(exam);
    }

    public void deleteById(Long id) {
        examRepository.deleteById(id);
    }

    // ========== Расписание осмотров ==========

    /**
     * Получить предстоящие осмотры
     */
    public List<VeterinaryExam> getUpcomingExams() {
        return examRepository.findByNextExamDateGreaterThanEqualOrderByNextExamDateAsc(LocalDate.now());
    }

    /**
     * Получить просроченные осмотры
     */
    public List<VeterinaryExam> getOverdueExams() {
        return examRepository.findByNextExamDateLessThanOrderByNextExamDateAsc(LocalDate.now());
    }

    /**
     * Животные, требующие осмотра (нет осмотров более 30 дней)
     */
    public List<Animal> getAnimalsNeedingExam() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        return animalRepository.findAll().stream()
                .filter(animal -> {
                    VeterinaryExam lastExam = examRepository
                            .findFirstByAnimalIdOrderByExamDateDesc(animal.getId());
                    return lastExam == null || lastExam.getExamDate().isBefore(thirtyDaysAgo);
                })
                .toList();
    }

    // ========== Статистика ==========

    public long getTotalExams() {
        return examRepository.count();
    }

    public long getExamsByStatus(VeterinaryExam.HealthStatus status) {
        return examRepository.findByHealthStatus(status).size();
    }

    public long getHealthyCount() {
        return getExamsByStatus(VeterinaryExam.HealthStatus.HEALTHY);
    }

    public long getSickCount() {
        return getExamsByStatus(VeterinaryExam.HealthStatus.SICK) +
                getExamsByStatus(VeterinaryExam.HealthStatus.ATTENTION);
    }

    /**
     * Последний осмотр животного
     */
    public VeterinaryExam getLastExam(Long animalId) {
        return examRepository.findFirstByAnimalIdOrderByExamDateDesc(animalId);
    }

    /**
     * Преобразовать в DTO
     */
    public VeterinaryExamDto toDto(VeterinaryExam exam) {
        VeterinaryExamDto dto = new VeterinaryExamDto();
        dto.setId(exam.getId());
        dto.setAnimalId(exam.getAnimal().getId());
        dto.setVeterinarianId(exam.getVeterinarian().getId());
        dto.setExamDate(exam.getExamDate());
        dto.setWeight(exam.getWeight());
        dto.setTemperature(exam.getTemperature());
        dto.setHeartRate(exam.getHeartRate());
        dto.setRespiratoryRate(exam.getRespiratoryRate());
        dto.setHealthStatus(exam.getHealthStatus());
        dto.setDiagnosis(exam.getDiagnosis());
        dto.setTreatment(exam.getTreatment());
        dto.setNotes(exam.getNotes());
        dto.setNextExamDate(exam.getNextExamDate());
        return dto;
    }
}
