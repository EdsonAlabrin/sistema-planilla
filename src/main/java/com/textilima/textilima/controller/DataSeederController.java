package com.textilima.textilima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.textilima.textilima.service.DataSeederService;

@Controller
@RequestMapping("/admin/data-seeder")
public class DataSeederController {
    @Autowired
    private DataSeederService dataSeederService;

    // Método para mostrar la página con los botones
    @GetMapping
    public String showSeederPage(Model model) {
        // Puedes agregar atributos al modelo si necesitas mostrar algo en la página inicial
        return "admin/data-seeder"; // Ruta a tu archivo HTML/Thymeleaf (ej. src/main/resources/templates/admin/data-seeder.html)
    }

    // Endpoint para limpiar datos
    @PostMapping("/clear")
    public String clearData(RedirectAttributes redirectAttributes) {
        try {
            dataSeederService.clearAllData();
            redirectAttributes.addFlashAttribute("successMessage", "Todos los datos han sido limpiados correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al limpiar los datos: " + e.getMessage());
            System.err.println("Error al limpiar datos: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/data-seeder"; // Redirige de vuelta a la página del seeder
    }

    // Endpoint para generar datos
    @PostMapping("/generate")
    public String generateData(RedirectAttributes redirectAttributes) {
        try {
            dataSeederService.generateAllDemoData();
            redirectAttributes.addFlashAttribute("successMessage", "Datos demo generados correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al generar los datos demo: " + e.getMessage());
            System.err.println("Error al generar datos: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/data-seeder"; // Redirige de vuelta a la página del seeder
    }
}
