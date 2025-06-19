package com.textilima.textilima.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.textilima.textilima.entities.Puesto;
import com.textilima.textilima.service.PuestoService;


import org.springframework.ui.Model;


@Controller
@RequestMapping("/puestos")
public class PuestoController {

    
    private final PuestoService puestoService;

    // Inyección de dependencia del servicio PuestoService
    @Autowired
    public PuestoController(PuestoService puestoService) {
        this.puestoService = puestoService;
    }

    // Muestra la lista de todos los puestos
    @GetMapping("/listar")
    public String listarPuestos(Model model) {
        List<Puesto> puestos = puestoService.listarTodosLosPuestos();
        model.addAttribute("puestos", puestos);
        return "puestos/listar"; // Nombre de la vista Thymeleaf (ej. src/main/resources/templates/puestos/list.html)
    }

    // Muestra el formulario para crear un nuevo puesto
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("puesto", new Puesto());
        return "puestos/form"; // Nombre de la vista Thymeleaf (ej. src/main/resources/templates/puestos/form.html)
    }

    // Procesa el envío del formulario para crear o actualizar un puesto
    @PostMapping("/guardar")
    public String guardarPuesto(@ModelAttribute("puesto") Puesto puesto, RedirectAttributes redirectAttributes) {
        try {
            if (puesto.getIdPuesto() == null) {
                puestoService.crearPuesto(puesto);
                redirectAttributes.addFlashAttribute("successMessage", "Puesto creado exitosamente.");
            } else {
                puestoService.actualizarPuesto(puesto.getIdPuesto(), puesto);
                redirectAttributes.addFlashAttribute("successMessage", "Puesto actualizado exitosamente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el puesto: " + e.getMessage());
            return "redirect:/puestos/nuevo"; // Redirige de nuevo al formulario en caso de error
        }
        return "redirect:/puestos/listar"; // Redirige a la lista después de guardar
    }

    // Muestra el formulario para editar un puesto existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Puesto puesto = puestoService.obtenerPuestoPorId(id);
        if (puesto == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Puesto no encontrado.");
            return "redirect:/puestos/editar";
        }
        model.addAttribute("puesto", puesto);
        return "puestos/registro";
    }

    // Elimina un puesto
    @GetMapping("/eliminar/{id}")
    public String eliminarPuesto(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            puestoService.eliminarPuesto(id);
            redirectAttributes.addFlashAttribute("successMessage", "Puesto eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el puesto: " + e.getMessage());
        }
        return "redirect:/puestos/listar";
    }

    // Muestra los detalles de un puesto
    @GetMapping("/detalles/{id}")
    public String verDetallesPuesto(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Puesto puesto = puestoService.obtenerPuestoPorId(id);
        if (puesto == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Puesto no encontrado.");
            return "redirect:/puestos/listar";
        }
        model.addAttribute("puesto", puesto);
        return "puestos/detalles"; // Nombre de la vista Thymeleaf (ej. src/main/resources/templates/puestos/details.html)
    }

    
}
