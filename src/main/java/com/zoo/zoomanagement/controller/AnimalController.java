package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.dto.AnimalDto;
import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.model.Enclosure;
import com.zoo.zoomanagement.model.Species;
import com.zoo.zoomanagement.repository.AnimalRepository;
import com.zoo.zoomanagement.repository.EnclosureRepository;
import com.zoo.zoomanagement.repository.SpeciesRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping
    public String listAnimals(Model model) {
        model.addAttribute("animals", animalRepository.findAll());
        return "animals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("animalDto", new AnimalDto());
        model.addAttribute("speciesList", speciesRepository.findAll());
        model.addAttribute("enclosuresList", enclosureRepository.findAll());
        return "animals/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный ID животного: " + id));

        // Преобразуем Animal в AnimalDto
        AnimalDto dto = new AnimalDto();
        dto.setId(animal.getId());
        dto.setName(animal.getName());
        dto.setGender(animal.getGender());
        dto.setBirthDate(animal.getBirthDate());
        dto.setArrivalDate(animal.getArrivalDate());
        if (animal.getSpecies() != null) {
            dto.setSpeciesId(animal.getSpecies().getId());
        }
        if (animal.getEnclosure() != null) {
            dto.setEnclosureId(animal.getEnclosure().getId());
        }
        dto.setPhotoUrl(animal.getPhotoUrl());

        model.addAttribute("animalDto", dto);
        model.addAttribute("speciesList", speciesRepository.findAll());
        model.addAttribute("enclosuresList", enclosureRepository.findAll());
        return "animals/form";
    }

    @PostMapping
    public String saveAnimal(@Valid @ModelAttribute("animalDto") AnimalDto animalDto,
                             BindingResult bindingResult,
                             @RequestParam(value = "photo", required = false) MultipartFile photo,
                             Model model) {

        // Если есть ошибки валидации — возвращаем форму с ошибками
        if (bindingResult.hasErrors()) {
            model.addAttribute("speciesList", speciesRepository.findAll());
            model.addAttribute("enclosuresList", enclosureRepository.findAll());
            return "animals/form";
        }

        // Создаём или загружаем Animal
        Animal animal = new Animal();
        if (animalDto.getId() != null) {
            animal = animalRepository.findById(animalDto.getId())
                    .orElse(new Animal());
        }

        // Заполняем поля из DTO
        animal.setName(animalDto.getName());
        animal.setGender(animalDto.getGender());
        animal.setBirthDate(animalDto.getBirthDate());
        animal.setArrivalDate(animalDto.getArrivalDate());
        animal.setPhotoUrl(animalDto.getPhotoUrl());

        // Загружаем связанные сущности
        if (animalDto.getSpeciesId() != null) {
            Species species = speciesRepository.findById(animalDto.getSpeciesId())
                    .orElseThrow(() -> new IllegalArgumentException("Неверный ID вида"));
            animal.setSpecies(species);
        }

        if (animalDto.getEnclosureId() != null) {
            Enclosure enclosure = enclosureRepository.findById(animalDto.getEnclosureId())
                    .orElse(null);
            animal.setEnclosure(enclosure);
        }

        // Обработка фото
        if (photo != null && !photo.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/images/animals/";
                Path path = Paths.get(uploadDir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Files.copy(photo.getInputStream(), path.resolve(fileName));
                animal.setPhotoUrl(fileName);
            } catch (IOException e) {
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
