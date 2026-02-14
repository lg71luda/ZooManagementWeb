package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.repository.StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffController(StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staffList", staffRepository.findAll());
        return "staff/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("roles", new String[]{"ADMIN", "CASHIER", "KEEPER", "VET"});
        return "staff/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неверный ID сотрудника: " + id));

        // Очищаем пароль для безопасности (не показываем хеш)
        staff.setPassword(null);

        model.addAttribute("staff", staff);
        model.addAttribute("roles", new String[]{"ADMIN", "CASHIER", "KEEPER", "VET"});
        return "staff/form";
    }

    @PostMapping
    public String saveStaff(@ModelAttribute Staff staff,
                            @RequestParam(required = false) String newPassword) {

        if (staff.getId() != null) {
            // Редактирование существующего сотрудника
            Staff existing = staffRepository.findById(staff.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

            existing.setName(staff.getName());
            existing.setLogin(staff.getLogin());
            existing.setRole(staff.getRole());

            // Обновляем пароль только если введён новый
            if (newPassword != null && !newPassword.isBlank()) {
                existing.setPassword(passwordEncoder.encode(newPassword));
            }

            staffRepository.save(existing);
        } else {
            // Создание нового сотрудника
            if (staff.getPassword() != null && !staff.getPassword().isBlank()) {
                staff.setPassword(passwordEncoder.encode(staff.getPassword()));
            } else {
                // Пароль по умолчанию
                staff.setPassword(passwordEncoder.encode("123456"));
            }
            staffRepository.save(staff);
        }

        return "redirect:/staff";
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffRepository.deleteById(id);
        return "redirect:/staff";
    }
}

