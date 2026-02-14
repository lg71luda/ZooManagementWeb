package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staffList", staffService.findAll());
        return "staff/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("roles", staffService.getAvailableRoles());
        return "staff/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Staff staff = staffService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден: " + id));

        staff.setPassword(null); // Не показываем хеш

        model.addAttribute("staff", staff);
        model.addAttribute("roles", staffService.getAvailableRoles());
        return "staff/form";
    }

    @PostMapping
    public String saveStaff(@ModelAttribute Staff staff,
                            @RequestParam(required = false) String newPassword) {

        if (staff.getId() != null) {
            staffService.update(staff.getId(), staff, newPassword);
        } else {
            staffService.create(staff);
        }

        return "redirect:/staff";
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffService.deleteById(id);
        return "redirect:/staff";
    }
}


