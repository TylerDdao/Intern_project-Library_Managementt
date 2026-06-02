package com.example.library_management.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/welcome")
    public String welcomePage(Model model) {
        model.addAttribute("username", "Alex");
        return "welcome"; // Maps to welcome.html
    }
}