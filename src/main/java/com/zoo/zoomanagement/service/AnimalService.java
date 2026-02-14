package com.zoo.zoomanagement.service;

import com.zoo.zoomanagement.dto.AnimalDto;
import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.model.Enclosure;
import com.zoo.zoomanagement.model.Species;
import com.zoo.zoomanagement.repository.AnimalRepository;
import com.zoo.zoomanagement.repository.EnclosureRepository;
import com.zoo.zoomanagement.repository.SpeciesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с животными
 * Содержит бизнес-логику создания, обновления и удаления животных
 */
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final SpeciesRepository speciesRepository;
    private final EnclosureRepository enclosureRepository;

    // Папка для хранения фото
    private static final String UPLOAD_DIR = "src/main/resources/static/images/animals/";

    public AnimalService(AnimalRepository animalRepository,
                         SpeciesRepository speciesRepository,
                         EnclosureRepository enclosureRepository) {
        this.animalRepository = animalRepository;
        this.speciesRepository = speciesRepository;
        this.enclosureRepository = enclosureRepository;
    }

    /**
     * Получить всех животных
     */
    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    /**
     * Найти животное по ID
     */
    public Optional<Animal> findById(Long id) {
        return animalRepository.findById(id);
    }

    /**
     * Получить все виды
     */
    public List<Species> findAllSpecies() {
        return speciesRepository.findAll();
    }

    /**
     * Получить все вольеры
     */
    public List<Enclosure> findAllEnclosures() {
        return enclosureRepository.findAll();
    }

    /**
     * Создать животное из DTO
     */
    public Animal createFromDto(AnimalDto dto, MultipartFile photo) throws IOException {
        Animal animal = new Animal();

        // Заполняем поля
        animal.setName(dto.getName());
        animal.setGender(dto.getGender());
        animal.setBirthDate(dto.getBirthDate());
        animal.setArrivalDate(dto.getArrivalDate());

        // Устанавливаем вид
        if (dto.getSpeciesId() != null) {
            Species species = speciesRepository.findById(dto.getSpeciesId())
                    .orElseThrow(() -> new IllegalArgumentException("Вид не найден: " + dto.getSpeciesId()));
            animal.setSpecies(species);
        }

        // Устанавливаем вольер
        if (dto.getEnclosureId() != null) {
            Enclosure enclosure = enclosureRepository.findById(dto.getEnclosureId())
                    .orElse(null);
            animal.setEnclosure(enclosure);
        }

        // Обрабатываем фото
        if (photo != null && !photo.isEmpty()) {
            String fileName = savePhoto(photo);
            animal.setPhotoUrl(fileName);
        } else if (dto.getPhotoUrl() != null) {
            animal.setPhotoUrl(dto.getPhotoUrl());
        }

        return animalRepository.save(animal);
    }

    /**
     * Обновить животное из DTO
     */
    public Animal updateFromDto(Long id, AnimalDto dto, MultipartFile photo) throws IOException {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Животное не найдено: " + id));

        // Обновляем поля
        animal.setName(dto.getName());
        animal.setGender(dto.getGender());
        animal.setBirthDate(dto.getBirthDate());
        animal.setArrivalDate(dto.getArrivalDate());

        // Обновляем вид
        if (dto.getSpeciesId() != null) {
            Species species = speciesRepository.findById(dto.getSpeciesId())
                    .orElseThrow(() -> new IllegalArgumentException("Вид не найден: " + dto.getSpeciesId()));
            animal.setSpecies(species);
        }

        // Обновляем вольер
        if (dto.getEnclosureId() != null) {
            Enclosure enclosure = enclosureRepository.findById(dto.getEnclosureId())
                    .orElse(null);
            animal.setEnclosure(enclosure);
        }

        // Обрабатываем новое фото
        if (photo != null && !photo.isEmpty()) {
            String fileName = savePhoto(photo);
            animal.setPhotoUrl(fileName);
        }

        return animalRepository.save(animal);
    }

    /**
     * Сохранить фото на диск
     */
    private String savePhoto(MultipartFile photo) throws IOException {
        Path path = Paths.get(UPLOAD_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        Files.copy(photo.getInputStream(), path.resolve(fileName));

        return fileName;
    }

    /**
     * Удалить животное по ID
     */
    public void deleteById(Long id) {
        animalRepository.deleteById(id);
    }

    /**
     * Преобразовать Entity в DTO
     */
    public AnimalDto toDto(Animal animal) {
        AnimalDto dto = new AnimalDto();
        dto.setId(animal.getId());
        dto.setName(animal.getName());
        dto.setGender(animal.getGender());
        dto.setBirthDate(animal.getBirthDate());
        dto.setArrivalDate(animal.getArrivalDate());
        dto.setPhotoUrl(animal.getPhotoUrl());

        if (animal.getSpecies() != null) {
            dto.setSpeciesId(animal.getSpecies().getId());
        }
        if (animal.getEnclosure() != null) {
            dto.setEnclosureId(animal.getEnclosure().getId());
        }

        return dto;
    }
}
