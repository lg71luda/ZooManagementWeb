package com.zoo.zoomanagement.service;

import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.repository.StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с сотрудниками
 */
@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    // Пароль по умолчанию для новых сотрудников
    private static final String DEFAULT_PASSWORD = "123456";

    public StaffService(StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Получить всех сотрудников
     */
    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    /**
     * Найти сотрудника по ID
     */
    public Optional<Staff> findById(Long id) {
        return staffRepository.findById(id);
    }

    /**
     * Найти сотрудника по логину
     */
    public Optional<Staff> findByLogin(String login) {
        return staffRepository.findByLogin(login);
    }

    /**
     * Создать нового сотрудника
     */
    public Staff create(Staff staff) {
        // Хешируем пароль
        if (staff.getPassword() != null && !staff.getPassword().isBlank()) {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
        } else {
            staff.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        }

        return staffRepository.save(staff);
    }

    /**
     * Обновить сотрудника
     */
    public Staff update(Long id, Staff updatedStaff, String newPassword) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден: " + id));

        staff.setName(updatedStaff.getName());
        staff.setLogin(updatedStaff.getLogin());
        staff.setRole(updatedStaff.getRole());

        // Обновляем пароль только если указан новый
        if (newPassword != null && !newPassword.isBlank()) {
            staff.setPassword(passwordEncoder.encode(newPassword));
        }

        return staffRepository.save(staff);
    }

    /**
     * Удалить сотрудника
     */
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }

    /**
     * Проверить существование логина
     */
    public boolean existsByLogin(String login) {
        return staffRepository.existsByLogin(login);
    }

    /**
     * Получить доступные роли
     */
    public String[] getAvailableRoles() {
        return new String[]{"ADMIN", "CASHIER", "KEEPER", "VET"};
    }
}
