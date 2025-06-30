package com.textilima.textilima.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.textilima.textilima.model.ParametroLegal;
import com.textilima.textilima.service.ParametroLegalService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/parametros-legales")
public class ParametroLegalController {
    private final ParametroLegalService parametroLegalService;

    @Autowired
    public ParametroLegalController(ParametroLegalService parametroLegalService) {
        this.parametroLegalService = parametroLegalService;
    }

    /**
     * Muestra la lista de todos los parámetros legales.
     * GET /parametros-legales/listar
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la vista Thymeleaf (parametros-legales/listar.html).
     */
    @GetMapping("/listar")
    public String listarParametrosLegales(Model model) {
        List<ParametroLegal> parametros = parametroLegalService.getAllParametrosLegales();
        model.addAttribute("parametros", parametros);
        return "parametros-legales/listar"; // Nombre de la vista Thymeleaf
    }

    /**
     * Muestra el formulario para crear un nuevo parámetro legal.
     * GET /parametros-legales/nuevo
     * @param model Objeto Model para pasar un ParametroLegal vacío a la vista.
     * @return El nombre de la vista Thymeleaf (parametros-legales/form.html).
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("parametro", new ParametroLegal());
        return "parametros-legales/form"; // Nombre de la vista Thymeleaf
    }

    /**
     * Procesa el envío del formulario para crear o actualizar un parámetro legal.
     * POST /parametros-legales/guardar
     * @param parametro El objeto ParametroLegal enviado desde el formulario.
     * @param redirectAttributes Para añadir mensajes flash que persisten en la redirección.
     * @return Una redirección a la lista de parámetros legales o al formulario en caso de error.
     */
    @PostMapping("/guardar")
    public String guardarParametroLegal(@ModelAttribute("parametro") ParametroLegal parametro, RedirectAttributes redirectAttributes) {
        try {
            // Se puede añadir lógica de validación adicional aquí (ej. fechas de vigencia)
            parametroLegalService.saveParametroLegal(parametro); // saveParametroLegal maneja creación y actualización
            redirectAttributes.addFlashAttribute("successMessage", "Parámetro legal guardado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el parámetro legal: " + e.getMessage());
            return "redirect:/parametros-legales/nuevo"; // Redirige de nuevo al formulario en caso de error
        }
        return "redirect:/parametros-legales/listar"; // Redirige a la lista después de guardar
    }

    /**
     * Muestra el formulario para editar un parámetro legal existente.
     * GET /parametros-legales/editar/{id}
     * @param id El ID del parámetro legal a editar.
     * @param model Objeto Model para pasar el ParametroLegal a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el parámetro no se encuentra.
     * @return El nombre de la vista Thymeleaf (parametros-legales/form.html) o una redirección.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ParametroLegal> parametro = parametroLegalService.getParametroLegalById(id);
        if (parametro.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Parámetro legal no encontrado.");
            return "redirect:/parametros-legales/listar";
        }
        model.addAttribute("parametro", parametro.get());
        return "parametros-legales/form";
    }

    /**
     * Elimina un parámetro legal.
     * GET /parametros-legales/eliminar/{id}
     * @param id El ID del parámetro legal a eliminar.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Una redirección a la lista de parámetros legales.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarParametroLegal(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            parametroLegalService.deleteParametroLegal(id);
            redirectAttributes.addFlashAttribute("successMessage", "Parámetro legal eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el parámetro legal: " + e.getMessage());
        }
        return "redirect:/parametros-legales/listar";
    }

    /**
     * Muestra los detalles de un parámetro legal.
     * GET /parametros-legales/detalles/{id}
     * @param id El ID del parámetro legal a mostrar.
     * @param model Objeto Model para pasar el ParametroLegal a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el parámetro no se encuentra.
     * @return El nombre de la vista Thymeleaf (parametros-legales/detalles.html) o una redirección.
     */
    @GetMapping("/detalles/{id}")
    public String verDetallesParametroLegal(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ParametroLegal> parametro = parametroLegalService.getParametroLegalById(id);
        if (parametro.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Parámetro legal no encontrado.");
            return "redirect:/parametros-legales/listar";
        }
        model.addAttribute("parametro", parametro.get());
        return "parametros-legales/detalles";
    }
}
