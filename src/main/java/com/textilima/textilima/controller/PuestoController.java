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

import com.textilima.textilima.model.Puesto;
import com.textilima.textilima.service.PuestoService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/puestos")
public class PuestoController {

    private final PuestoService puestoService;

    @Autowired
    public PuestoController(PuestoService puestoService) {
        this.puestoService = puestoService;
    }

    /**
     * Muestra la lista de todos los puestos.
     * GET /puestos/listar
     * 
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la vista Thymeleaf (puestos/listar.html).
     */
    @GetMapping("/listar")
    public String listarPuestos(Model model) {
        List<Puesto> puestos = puestoService.listarTodosLosPuestos();
        model.addAttribute("puestos", puestos);
        return "puestos/listar";
    }

    /**
     * Muestra el formulario para crear un nuevo puesto.
     * GET /puestos/nuevo
     * 
     * @param model Objeto Model para pasar un Puesto vacío a la vista.
     * @return El nombre de la vista Thymeleaf (puestos/form.html).
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("puesto", new Puesto());
        return "puestos/form";
    }

    /**
     * Procesa el envío del formulario para crear o actualizar un puesto.
     * POST /puestos/guardar
     * 
     * @param puesto             El objeto Puesto enviado desde el formulario.
     * @param redirectAttributes Para añadir mensajes flash que persisten en la
     *                           redirección.
     * @return Una redirección a la lista de puestos o al formulario en caso de
     *         error.
     */
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
            return "redirect:/puestos/nuevo";
        }
        return "redirect:/puestos/listar";
    }

    /**
     * Muestra el formulario para editar un puesto existente.
     * GET /puestos/editar/{id}
     * 
     * @param id                 El ID del puesto a editar.
     * @param model              Objeto Model para pasar el Puesto a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el puesto no se
     *                           encuentra.
     * @return El nombre de la vista Thymeleaf (puestos/form.html) o una
     *         redirección.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Puesto> puesto = puestoService.obtenerPuestoPorId(id);
        if (puesto.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Puesto no encontrado.");
            return "redirect:/puestos/listar";
        }
        model.addAttribute("puesto", puesto.get());
        return "puestos/form";
    }

    /**
     * Elimina un puesto por su ID.
     * GET /puestos/eliminar/{id}
     * 
     * @param id                 El ID del puesto a eliminar.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Una redirección a la lista de puestos.
     */
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

    /**
     * Muestra los detalles de un puesto.
     * GET /puestos/detalles/{id}
     * 
     * @param id                 El ID del puesto a mostrar.
     * @param model              Objeto Model para pasar el Puesto a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el puesto no se
     *                           encuentra.
     * @return El nombre de la vista Thymeleaf (puestos/detalles.html) o una
     *         redirección.
     */
    @GetMapping("/detalles/{id}")
    public String verDetallesPuesto(@PathVariable("id") Integer id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Puesto> puesto = puestoService.obtenerPuestoPorId(id);
        if (puesto.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Puesto no encontrado.");
            return "redirect:/puestos/listar";
        }
        model.addAttribute("puesto", puesto.get());
        return "puestos/detalles";
    }

}
