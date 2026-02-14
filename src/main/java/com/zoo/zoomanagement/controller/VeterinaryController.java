package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.VeterinaryExamDto;
import com.zoo.zoomanagement.model.VeterinaryExam;
import com.zoo.zoomanagement.service.VeterinaryService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/veterinary")
public class VeterinaryController {

    private final VeterinaryService veterinaryService;
    private final com.zoo.zoomanagement.repository.AnimalRepository animalRepository;

    public VeterinaryController(VeterinaryService veterinaryService,
                                com.zoo.zoomanagement.repository.AnimalRepository animalRepository) {
        this.veterinaryService = veterinaryService;
        this.animalRepository = animalRepository;
    }

    // ========== Журнал осмотров ==========

    @GetMapping
    public String list(
            @RequestParam(required = false) Long animalId,
            @RequestParam(required = false) String status,
            Model model) {

        List<VeterinaryExam> exams;

        if (animalId != null) {
            exams = veterinaryService.findByAnimalId(animalId);
        } else if (status != null && !status.isEmpty()) {
            VeterinaryExam.HealthStatus healthStatus = VeterinaryExam.HealthStatus.valueOf(status);
            exams = veterinaryService.findAll().stream()
                    .filter(e -> e.getHealthStatus() == healthStatus)
                    .toList();
        } else {
            exams = veterinaryService.findAll();
        }

        model.addAttribute("exams", exams);
        model.addAttribute("animals", animalRepository.findAll());
        model.addAttribute("selectedAnimalId", animalId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", VeterinaryExam.HealthStatus.values());

        return "veterinary/list";
    }

    // ========== Новый осмотр ==========

    @GetMapping("/new")
    public String showCreateForm(
            @RequestParam(required = false) Long animalId,
            Model model) {

        VeterinaryExamDto dto = new VeterinaryExamDto();
        dto.setExamDate(java.time.LocalDateTime.now());
        if (animalId != null) {
            dto.setAnimalId(animalId);
        }

        model.addAttribute("examDto", dto);
        model.addAttribute("animals", animalRepository.findAll());
        model.addAttribute("statuses", VeterinaryExam.HealthStatus.values());
        return "veterinary/form";
    }

    @PostMapping
    public String createExam(
            @Valid @ModelAttribute("examDto") VeterinaryExamDto dto,
            BindingResult bindingResult,
            Authentication authentication,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("animals", animalRepository.findAll());
            model.addAttribute("statuses", VeterinaryExam.HealthStatus.values());
            return "veterinary/form";
        }

        veterinaryService.createFromDto(dto, authentication.getName());
        return "redirect:/veterinary";
    }

    // ========== Редактирование ==========

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        VeterinaryExam exam = veterinaryService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Осмотр не найден: " + id));

        model.addAttribute("examDto", veterinaryService.toDto(exam));
        model.addAttribute("animals", animalRepository.findAll());
        model.addAttribute("statuses", VeterinaryExam.HealthStatus.values());
        model.addAttribute("exam", exam);
        return "veterinary/form";
    }

    @PostMapping("/update/{id}")
    public String updateExam(
            @PathVariable Long id,
            @Valid @ModelAttribute("examDto") VeterinaryExamDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("animals", animalRepository.findAll());
            model.addAttribute("statuses", VeterinaryExam.HealthStatus.values());
            return "veterinary/form";
        }

        veterinaryService.updateFromDto(id, dto);
        return "redirect:/veterinary";
    }

    // ========== Удаление ==========

    @PostMapping("/delete/{id}")
    public String deleteExam(@PathVariable Long id) {
        veterinaryService.deleteById(id);
        return "redirect:/veterinary";
    }

    // ========== Расписание осмотров ==========

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("upcomingExams", veterinaryService.getUpcomingExams());
        model.addAttribute("overdueExams", veterinaryService.getOverdueExams());
        model.addAttribute("animalsNeedingExam", veterinaryService.getAnimalsNeedingExam());
        return "veterinary/schedule";
    }

    // ========== История осмотров животного ==========

    @GetMapping("/animal/{animalId}")
    public String animalHistory(@PathVariable Long animalId, Model model) {
        model.addAttribute("exams", veterinaryService.findByAnimalId(animalId));
        model.addAttribute("animal", animalRepository.findById(animalId)
                .orElseThrow(() -> new IllegalArgumentException("Животное не найдено")));
        model.addAttribute("lastExam", veterinaryService.getLastExam(animalId));
        return "veterinary/animal-history";
    }

    // ========== Статистика ==========

    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("totalExams", veterinaryService.getTotalExams());
        model.addAttribute("healthyCount", veterinaryService.getExamsByStatus(VeterinaryExam.HealthStatus.HEALTHY));
        model.addAttribute("attentionCount", veterinaryService.getExamsByStatus(VeterinaryExam.HealthStatus.ATTENTION));
        model.addAttribute("sickCount", veterinaryService.getExamsByStatus(VeterinaryExam.HealthStatus.SICK));
        model.addAttribute("recoveringCount", veterinaryService.getExamsByStatus(VeterinaryExam.HealthStatus.RECOVERING));
        model.addAttribute("quarantineCount", veterinaryService.getExamsByStatus(VeterinaryExam.HealthStatus.QUARANTINE));
        model.addAttribute("overdueCount", veterinaryService.getOverdueExams().size());

        return "veterinary/stats";
    }
}

