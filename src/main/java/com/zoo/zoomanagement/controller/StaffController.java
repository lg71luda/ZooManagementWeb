package com.zoo.zoomanagement.controller;

import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.repository.StaffRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffRepository staffRepository;

    public StaffController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @GetMapping
    public String listStaff(Model model) {
        model.addAttribute("staffList", staffRepository.findAll());
        return "staff/list";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/staff";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid staff Id:" + id));
        model.addAttribute("staff", staff);
        return "staff/form";
    }

    @PostMapping
    public String saveStaff(@ModelAttribute Staff staff) {
        staffRepository.save(staff);
        return "redirect:/staff";
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffRepository.deleteById(id);
        return "redirect:/staff";
    }
}
