package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.AnimalDto;
import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.service.AnimalService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public String listAnimals(Model model) {
        model.addAttribute("animals", animalService.findAll());
        return "animals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("animalDto", new AnimalDto());
        model.addAttribute("speciesList", animalService.findAllSpecies());
        model.addAttribute("enclosuresList", animalService.findAllEnclosures());
        return "animals/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Animal animal = animalService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Животное не найдено: " + id));

        model.addAttribute("animalDto", animalService.toDto(animal));
        model.addAttribute("speciesList", animalService.findAllSpecies());
        model.addAttribute("enclosuresList", animalService.findAllEnclosures());
        return "animals/form";
    }

    @PostMapping
    public String saveAnimal(@Valid @ModelAttribute("animalDto") AnimalDto animalDto,
                             BindingResult bindingResult,
                             @RequestParam(value = "photo", required = false) MultipartFile photo,
                             Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("speciesList", animalService.findAllSpecies());
            model.addAttribute("enclosuresList", animalService.findAllEnclosures());
            return "animals/form";
        }

        if (animalDto.getId() != null) {
            animalService.updateFromDto(animalDto.getId(), animalDto, photo);
        } else {
            animalService.createFromDto(animalDto, photo);
        }

        return "redirect:/animals";
    }

    @PostMapping("/delete/{id}")
    public String deleteAnimal(@PathVariable Long id) {
        animalService.deleteById(id);
        return "redirect:/animals";
    }
}

