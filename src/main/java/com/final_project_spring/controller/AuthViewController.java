package com.final_project_spring.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class AuthViewController {

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register"; // Возвращает имя HTML файла (например, register.html в папке resources/templates)
    }
}

