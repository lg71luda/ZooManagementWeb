package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.FeedingDto;
import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.model.Feeding;
import com.zoo.zoomanagement.repository.AnimalRepository;
import com.zoo.zoomanagement.repository.FeedingRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/feedings")
public class FeedingController {

    private final FeedingRepository feedingRepository;
    private final AnimalRepository animalRepository;

    public FeedingController(FeedingRepository feedingRepository, AnimalRepository animalRepository) {
        this.feedingRepository = feedingRepository;
        this.animalRepository = animalRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("feedings", feedingRepository.findAll());
        return "feedings/list";
    }

    @GetMapping("/new")
    public String newFeeding(Model model) {
        model.addAttribute("feedingDto", new FeedingDto());
        model.addAttribute("animals", animalRepository.findAll());
        return "feedings/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("feedingDto") FeedingDto feedingDto,
                       BindingResult bindingResult,
                       Model model) {

        // Если есть ошибки валидации
        if (bindingResult.hasErrors()) {
            model.addAttribute("animals", animalRepository.findAll());
            return "feedings/form";
        }

        // Создаём Feeding из DTO
        Feeding feeding = new Feeding();
        feeding.setFeedType(feedingDto.getFeedType());
        feeding.setQuantity(feedingDto.getQuantity());
        feeding.setTime(feedingDto.getTime());
        feeding.setNotes(feedingDto.getNotes());

        // Загружаем Animal
        if (feedingDto.getAnimalId() != null) {
            Animal animal = animalRepository.findById(feedingDto.getAnimalId())
                    .orElseThrow(() -> new IllegalArgumentException("Неверный ID животного"));
            feeding.setAnimal(animal);
        }

        feedingRepository.save(feeding);
        return "redirect:/feedings";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        List<Feeding> all = feedingRepository.findAll();

        double totalKg = all.stream().mapToDouble(Feeding::getQuantity).sum();
        long totalFeedings = all.size();

        Map<String, Double> byType = all.stream()
                .collect(Collectors.groupingBy(
                        Feeding::getFeedType,
                        Collectors.summingDouble(Feeding::getQuantity)
                ));

        List<Map.Entry<String, Double>> sorted = byType.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        model.addAttribute("totalKg", totalKg);
        model.addAttribute("totalFeedings", totalFeedings);
        model.addAttribute("byType", sorted);
        model.addAttribute("hasData", !sorted.isEmpty());

        return "feedings/stats";
    }
}

