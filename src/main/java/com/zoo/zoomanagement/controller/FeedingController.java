package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.model.Feeding;
import com.zoo.zoomanagement.repository.FeedingRepository;
import com.zoo.zoomanagement.repository.AnimalRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        model.addAttribute("feeding", new Feeding());
        model.addAttribute("animals", animalRepository.findAll());
        return "feedings/form";
    }

    @PostMapping
    public String save(@ModelAttribute Feeding feeding) {
        feedingRepository.save(feeding);
        return "redirect:/feedings";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        List<Feeding> all = feedingRepository.findAll();

        // Общая статистика
        double totalKg = all.stream().mapToDouble(Feeding::getQuantity).sum();
        long totalFeedings = all.size();

        // По типам корма
        Map<String, Double> byType = all.stream()
                .collect(Collectors.groupingBy(
                        Feeding::getFeedType,
                        Collectors.summingDouble(Feeding::getQuantity)
                ));

        // Сортируем по убыванию
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
