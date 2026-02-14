package com.zoo.zoomanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final com.zoo.zoomanagement.repository.AnimalRepository animalRepository;
    private final com.zoo.zoomanagement.repository.StaffRepository staffRepository;

    public HomeController(com.zoo.zoomanagement.repository.AnimalRepository animalRepository,
                          com.zoo.zoomanagement.repository.StaffRepository staffRepository) {
        this.animalRepository = animalRepository;
        this.staffRepository = staffRepository;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        // Данные пользователя
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            model.addAttribute("username", username);
            model.addAttribute("role", role);
        } else {
            model.addAttribute("username", "Гость");
            model.addAttribute("role", "UNKNOWN");
        }

        // Реальные данные из базы
        model.addAttribute("animalCount", animalRepository.count());
        model.addAttribute("staffCount", staffRepository.count());

        return "home";
    }
}
