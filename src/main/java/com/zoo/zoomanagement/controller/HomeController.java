package com.zoo.zoomanagement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        // Используем параметр Authentication
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            model.addAttribute("username", username);
            model.addAttribute("role", role);
        } else {
            model.addAttribute("username", "Гость");
            model.addAttribute("role", "UNKNOWN");
        }

        // Можно потом заменить на реальные цифры из репозиториев
        model.addAttribute("animalCount", 42);
        model.addAttribute("staffCount", 15);

        return "home";
    }
}