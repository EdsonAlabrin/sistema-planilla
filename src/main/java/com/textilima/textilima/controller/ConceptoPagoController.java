package com.textilima.textilima.controller;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;
import com.textilima.textilima.service.ConceptoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/conceptos") // Mapea todas las solicitudes que comienzan con /conceptos a este controlador
public class ConceptoPagoController {

    private final ConceptoPagoService conceptoPagoService;

    @Autowired
    public ConceptoPagoController(ConceptoPagoService conceptoPagoService) {
        this.conceptoPagoService = conceptoPagoService;
    }

    // Muestra la lista de todos los conceptos de pago
    @GetMapping // Mapea las solicitudes GET a /conceptos
    public String listConceptos(Model model) {
        List<ConceptoPago> conceptos = conceptoPagoService.findAll();
        model.addAttribute("conceptos", conceptos);
        return "conceptos/listar"; // Retorna la vista: src/main/resources/templates/conceptos/list.html
    }

    // Muestra el formulario para crear un nuevo concepto de pago
    @GetMapping("/nuevo")
    public String showNewConceptoForm(Model model) {
        model.addAttribute("conceptoPago", new ConceptoPago()); // Nueva instancia para el formulario
        model.addAttribute("tiposConcepto", TipoConcepto.values()); // Envía los valores del enum al formulario
        model.addAttribute("pageTitle", "Crear Nuevo Concepto de Pago");
        return "conceptos/form"; // Retorna el formulario
    }

    // Muestra el formulario para editar un concepto de pago existente
    @GetMapping("/editar/{id}")
    public String showEditConceptoForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<ConceptoPago> conceptoOptional = conceptoPagoService.findById(id);
        if (conceptoOptional.isPresent()) {
            model.addAttribute("conceptoPago", conceptoOptional.get());
            model.addAttribute("tiposConcepto", TipoConcepto.values());
            model.addAttribute("pageTitle", "Editar Concepto de Pago (ID: " + id + ")");
            return "conceptos/form";
        } else {
            ra.addFlashAttribute("errorMessage", "Concepto de Pago no encontrado con ID: " + id);
            return "redirect:/conceptos/listar";
        }
    }

    // Guarda un concepto de pago nuevo o actualizado
    @PostMapping("/guardar")
    public String saveConcepto(@ModelAttribute ConceptoPago conceptoPago, RedirectAttributes ra) {
        try {
            // Validación para evitar nombres de conceptos duplicados
            Optional<ConceptoPago> existingConcepto = conceptoPagoService.findByNombreConcepto(conceptoPago.getNombreConcepto());
            if (existingConcepto.isPresent() && (conceptoPago.getIdConcepto() == null || !existingConcepto.get().getIdConcepto().equals(conceptoPago.getIdConcepto()))) {
                ra.addFlashAttribute("errorMessage", "Ya existe un concepto de pago con el nombre: " + conceptoPago.getNombreConcepto());
                return "redirect:/conceptos/" + (conceptoPago.getIdConcepto() != null ? "editar/" + conceptoPago.getIdConcepto() : "nuevo");
            }

            conceptoPagoService.save(conceptoPago);
            ra.addFlashAttribute("successMessage", "Concepto de Pago guardado exitosamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error al guardar el concepto de pago: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/conceptos/" + (conceptoPago.getIdConcepto() != null ? "editar/" + conceptoPago.getIdConcepto() : "nuevo");
        }
        return "redirect:/conceptos";
    }

    // Elimina un concepto de pago por su ID
    @GetMapping("/eliminar/{id}")
    public String deleteConcepto(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            conceptoPagoService.deleteById(id);
            ra.addFlashAttribute("successMessage", "Concepto de Pago eliminado exitosamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error al eliminar el concepto de pago: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/conceptos";
    }

    // Muestra los detalles de un concepto de pago específico
    @GetMapping("/detalles/{id}")
    public String viewConceptoDetails(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<ConceptoPago> conceptoOptional = conceptoPagoService.findById(id);
        if (conceptoOptional.isPresent()) {
            model.addAttribute("conceptoPago", conceptoOptional.get());
            model.addAttribute("pageTitle", "Detalles del Concepto de Pago");
            return "conceptos/detalles"; // Retorna la vista de detalles
        } else {
            ra.addFlashAttribute("errorMessage", "Concepto de Pago no encontrado con ID: " + id);
            return "redirect:/conceptos";
        }
    }
}
