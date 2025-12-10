package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.repository.AnimalRepository;
import com.zoo.zoomanagement.repository.EnclosureRepository;
import com.zoo.zoomanagement.repository.SpeciesRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/animals")
public class AnimalController {

    private final AnimalRepository animalRepository;
    private final SpeciesRepository speciesRepository;
    private final EnclosureRepository enclosureRepository;

    public AnimalController(AnimalRepository animalRepository,
                            SpeciesRepository speciesRepository,
                            EnclosureRepository enclosureRepository) {
        this.animalRepository = animalRepository;
        this.speciesRepository = speciesRepository;
        this.enclosureRepository = enclosureRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/animals";
    }

    @GetMapping
    public String listAnimals(Model model) {
        model.addAttribute("animals", animalRepository.findAll());
        return "animals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("animal", new Animal());
        model.addAttribute("speciesList", speciesRepository.findAll());
        model.addAttribute("enclosuresList", enclosureRepository.findAll());
        return "animals/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid animal Id:" + id));
        model.addAttribute("animal", animal);
        model.addAttribute("speciesList", speciesRepository.findAll());
        model.addAttribute("enclosuresList", enclosureRepository.findAll());
        return "animals/form";
    }

    @PostMapping
    public String saveAnimal(@ModelAttribute Animal animal,
                             @RequestParam(value = "photo", required = false) MultipartFile photo) {

        if (photo != null && !photo.isEmpty()) {
            try {
                // Путь к папке с фото
                String uploadDir = "src/main/resources/static/images/animals/";
                Path path = Paths.get(uploadDir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Files.copy(photo.getInputStream(), path.resolve(fileName));

                animal.setPhotoUrl(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        animalRepository.save(animal);
        return "redirect:/animals";
    }

    @PostMapping("/delete/{id}")
    public String deleteAnimal(@PathVariable Long id) {
        animalRepository.deleteById(id);
        return "redirect:/animals";
    }
}