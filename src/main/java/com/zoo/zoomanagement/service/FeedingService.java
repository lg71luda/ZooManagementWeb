package com.zoo.zoomanagement.service;

import com.zoo.zoomanagement.dto.FeedingDto;
import com.zoo.zoomanagement.model.Animal;
import com.zoo.zoomanagement.model.Feeding;
import com.zoo.zoomanagement.repository.AnimalRepository;
import com.zoo.zoomanagement.repository.FeedingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для работы с кормлениями
 */
@Service
public class FeedingService {

    private final FeedingRepository feedingRepository;
    private final AnimalRepository animalRepository;

    public FeedingService(FeedingRepository feedingRepository, AnimalRepository animalRepository) {
        this.feedingRepository = feedingRepository;
        this.animalRepository = animalRepository;
    }

    /**
     * Получить все кормления
     */
    public List<Feeding> findAll() {
        return feedingRepository.findAll();
    }

    /**
     * Создать кормление из DTO
     */
    public Feeding createFromDto(FeedingDto dto) {
        Feeding feeding = new Feeding();
        feeding.setFeedType(dto.getFeedType());
        feeding.setQuantity(dto.getQuantity());
        feeding.setTime(dto.getTime());
        feeding.setNotes(dto.getNotes());

        if (dto.getAnimalId() != null) {
            Animal animal = animalRepository.findById(dto.getAnimalId())
                    .orElseThrow(() -> new IllegalArgumentException("Животное не найдено: " + dto.getAnimalId()));
            feeding.setAnimal(animal);
        }

        return feedingRepository.save(feeding);
    }

    /**
     * Получить общее количество корма (кг)
     */
    public double getTotalQuantity() {
        return feedingRepository.findAll().stream()
                .mapToDouble(Feeding::getQuantity)
                .sum();
    }

    /**
     * Получить количество кормлений
     */
    public long getTotalCount() {
        return feedingRepository.count();
    }

    /**
     * Получить статистику по типам корма
     */
    public Map<String, Double> getStatsByFeedType() {
        return feedingRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Feeding::getFeedType,
                        Collectors.summingDouble(Feeding::getQuantity)
                ));
    }

    /**
     * Получить отсортированную статистику по типам корма
     */
    public List<Map.Entry<String, Double>> getSortedStatsByFeedType() {
        return getStatsByFeedType().entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());
    }
}
