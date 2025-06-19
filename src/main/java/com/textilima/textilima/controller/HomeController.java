package com.textilima.textilima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index"; // Esto mapea a src/main/resources/templates/index.html
    }

    @GetMapping("/conocenos")
    public String conocenos() {
        return "nosotros"; // Esto mapea a src/main/resources/templates/nosotros.html
    }
}