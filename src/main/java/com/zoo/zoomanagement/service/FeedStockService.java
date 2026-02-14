package com.zoo.zoomanagement.service;

import com.zoo.zoomanagement.dto.FeedReceiptDto;
import com.zoo.zoomanagement.dto.FeedTypeDto;
import com.zoo.zoomanagement.model.FeedReceipt;
import com.zoo.zoomanagement.model.FeedStock;
import com.zoo.zoomanagement.model.FeedType;
import com.zoo.zoomanagement.repository.FeedReceiptRepository;
import com.zoo.zoomanagement.repository.FeedStockRepository;
import com.zoo.zoomanagement.repository.FeedTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления складом кормов
 */
@Service
public class FeedStockService {

    private final FeedTypeRepository feedTypeRepository;
    private final FeedStockRepository feedStockRepository;
    private final FeedReceiptRepository feedReceiptRepository;

    public FeedStockService(FeedTypeRepository feedTypeRepository,
                            FeedStockRepository feedStockRepository,
                            FeedReceiptRepository feedReceiptRepository) {
        this.feedTypeRepository = feedTypeRepository;
        this.feedStockRepository = feedStockRepository;
        this.feedReceiptRepository = feedReceiptRepository;
    }

    // ========== Виды корма ==========

    public List<FeedType> findAllFeedTypes() {
        return feedTypeRepository.findAll();
    }

    public Optional<FeedType> findFeedTypeById(Long id) {
        return feedTypeRepository.findById(id);
    }

    public FeedType createFeedType(FeedTypeDto dto) {
        if (feedTypeRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Вид корма с названием '" + dto.getName() + "' уже существует");
        }

        FeedType feedType = new FeedType();
        feedType.setName(dto.getName());
        feedType.setUnit(dto.getUnit());
        feedType.setMinStock(dto.getMinStock());
        feedType.setDescription(dto.getDescription());

        FeedType saved = feedTypeRepository.save(feedType);

        // Создаём запись на складе с нулевым остатком
        FeedStock stock = new FeedStock();
        stock.setFeedType(saved);
        stock.setQuantity(0.0);
        stock.setLastUpdated(LocalDateTime.now());
        feedStockRepository.save(stock);

        return saved;
    }

    public FeedType updateFeedType(Long id, FeedTypeDto dto) {
        FeedType feedType = feedTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Вид корма не найден: " + id));

        feedType.setName(dto.getName());
        feedType.setUnit(dto.getUnit());
        feedType.setMinStock(dto.getMinStock());
        feedType.setDescription(dto.getDescription());

        return feedTypeRepository.save(feedType);
    }

    public void deleteFeedType(Long id) {
        // Проверяем, есть ли остатки
        FeedStock stock = feedStockRepository.findByFeedTypeId(id).orElse(null);
        if (stock != null && stock.getQuantity() > 0) {
            throw new IllegalArgumentException("Нельзя удалить вид корма, если на складе есть остатки");
        }

        if (stock != null) {
            feedStockRepository.delete(stock);
        }
        feedTypeRepository.deleteById(id);
    }

    // ========== Остатки на складе ==========

    public List<FeedStock> findAllStock() {
        return feedStockRepository.findAll();
    }

    public Optional<FeedStock> findStockByFeedTypeId(Long feedTypeId) {
        return feedStockRepository.findByFeedTypeId(feedTypeId);
    }

    /**
     * Получить список кормов с низким остатком
     */
    public List<FeedStock> findLowStock() {
        return feedStockRepository.findAll().stream()
                .filter(FeedStock::isLowStock)
                .toList();
    }

    /**
     * Списать корм со склада (при кормлении)
     */
    @Transactional
    public boolean deductFromStock(Long feedTypeId, Double quantity) {
        FeedStock stock = feedStockRepository.findByFeedTypeId(feedTypeId)
                .orElse(null);

        if (stock == null || stock.getQuantity() < quantity) {
            return false; // Недостаточно на складе
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stock.setLastUpdated(LocalDateTime.now());
        feedStockRepository.save(stock);

        return true;
    }

    /**
     * Добавить корм на склад (при поступлении)
     */
    @Transactional
    public void addToStock(Long feedTypeId, Double quantity) {
        FeedStock stock = feedStockRepository.findByFeedTypeId(feedTypeId)
                .orElseGet(() -> {
                    FeedType feedType = feedTypeRepository.findById(feedTypeId)
                            .orElseThrow(() -> new IllegalArgumentException("Вид корма не найден"));
                    FeedStock newStock = new FeedStock();
                    newStock.setFeedType(feedType);
                    newStock.setQuantity(0.0);
                    return newStock;
                });

        stock.setQuantity(stock.getQuantity() + quantity);
        stock.setLastUpdated(LocalDateTime.now());
        feedStockRepository.save(stock);
    }

    // ========== Поступления ==========

    public List<FeedReceipt> findAllReceipts() {
        return feedReceiptRepository.findAll();
    }

    public List<FeedReceipt> findReceiptsByFeedType(Long feedTypeId) {
        return feedReceiptRepository.findByFeedTypeId(feedTypeId);
    }

    public List<FeedReceipt> findReceiptsByPeriod(LocalDate start, LocalDate end) {
        return feedReceiptRepository.findByReceiptDateBetween(start, end);
    }

    public FeedReceipt createReceipt(FeedReceiptDto dto) {
        FeedType feedType = feedTypeRepository.findById(dto.getFeedTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Вид корма не найден"));

        FeedReceipt receipt = new FeedReceipt();
        receipt.setFeedType(feedType);
        receipt.setQuantity(dto.getQuantity());
        receipt.setReceiptDate(dto.getReceiptDate());
        receipt.setSupplier(dto.getSupplier());
        receipt.setPrice(dto.getPrice());
        receipt.setInvoiceNumber(dto.getInvoiceNumber());
        receipt.setNotes(dto.getNotes());

        // Рассчитываем общую сумму
        if (dto.getPrice() != null && dto.getQuantity() != null) {
            receipt.setTotalPrice(dto.getPrice() * dto.getQuantity());
        }

        FeedReceipt saved = feedReceiptRepository.save(receipt);

        // Добавляем на склад
        addToStock(dto.getFeedTypeId(), dto.getQuantity());

        return saved;
    }

    // ========== Статистика ==========

    public double getTotalStockValue() {
        return feedStockRepository.findAll().stream()
                .mapToDouble(FeedStock::getQuantity)
                .sum();
    }

    public long getLowStockCount() {
        return findLowStock().size();
    }
}
