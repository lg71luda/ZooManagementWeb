package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.FeedingDto;
import com.zoo.zoomanagement.service.FeedingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedings")
public class FeedingController {

    private final FeedingService feedingService;
    private final com.zoo.zoomanagement.repository.AnimalRepository animalRepository;

    public FeedingController(FeedingService feedingService,
                             com.zoo.zoomanagement.repository.AnimalRepository animalRepository) {
        this.feedingService = feedingService;
        this.animalRepository = animalRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("feedings", feedingService.findAll());
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

        if (bindingResult.hasErrors()) {
            model.addAttribute("animals", animalRepository.findAll());
            return "feedings/form";
        }

        feedingService.createFromDto(feedingDto);
        return "redirect:/feedings";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("totalKg", feedingService.getTotalQuantity());
        model.addAttribute("totalFeedings", feedingService.getTotalCount());
        model.addAttribute("byType", feedingService.getSortedStatsByFeedType());
        model.addAttribute("hasData", feedingService.getTotalCount() > 0);
        return "feedings/stats";
    }
}


