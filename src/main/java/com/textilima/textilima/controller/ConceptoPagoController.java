package com.textilima.textilima.controller;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.MetodoCalculo;
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

    /**
     * Muestra la lista de todos los conceptos de pago.
     * GET /conceptospago/listar
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la vista Thymeleaf (conceptospago/listar.html).
     */
    @GetMapping("/listar")
    public String listarConceptosPago(Model model) {
        List<ConceptoPago> conceptosPago = conceptoPagoService.getAllConceptosPago();
        model.addAttribute("conceptosPago", conceptosPago);
        return "conceptospago/listar"; // Nombre de la vista Thymeleaf
    }

    /**
     * Muestra el formulario para crear un nuevo concepto de pago.
     * GET /conceptospago/nuevo
     * @param model Objeto Model para pasar un ConceptoPago vacío a la vista y listas para selects.
     * @return El nombre de la vista Thymeleaf (conceptospago/form.html).
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("conceptoPago", new ConceptoPago());
        model.addAttribute("tiposConcepto", TipoConcepto.values()); // Para el select de tipos
        model.addAttribute("metodosCalculo", MetodoCalculo.values()); // Para el select de métodos de cálculo
        return "conceptospago/form"; // Nombre de la vista Thymeleaf
    }

    /**
     * Procesa el envío del formulario para crear o actualizar un concepto de pago.
     * POST /conceptospago/guardar
     * @param conceptoPago El objeto ConceptoPago enviado desde el formulario.
     * @param redirectAttributes Para añadir mensajes flash que persisten en la redirección.
     * @return Una redirección a la lista de conceptos de pago o al formulario en caso de error.
     */
    @PostMapping("/guardar")
    public String guardarConceptoPago(@ModelAttribute("conceptoPago") ConceptoPago conceptoPago, RedirectAttributes redirectAttributes) {
        try {
            // Validación de unicidad por nombre de concepto (ignora si es el mismo objeto al editar)
            Optional<ConceptoPago> existingConcepto = conceptoPagoService.getConceptoPagoByNombre(conceptoPago.getNombreConcepto());
            if (existingConcepto.isPresent() && (conceptoPago.getIdConcepto() == null || !existingConcepto.get().getIdConcepto().equals(conceptoPago.getIdConcepto()))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ya existe un concepto de pago con el nombre '" + conceptoPago.getNombreConcepto() + "'.");
                return "redirect:/conceptospago/nuevo"; // O redirigir a editar si es un conflicto al editar
            }

            conceptoPagoService.saveConceptoPago(conceptoPago); // saveConceptoPago maneja creación y actualización
            redirectAttributes.addFlashAttribute("successMessage", "Concepto de pago guardado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el concepto de pago: " + e.getMessage());
            // En caso de error, volvemos al formulario para que el usuario corrija
            if (conceptoPago.getIdConcepto() != null) {
                return "redirect:/conceptospago/editar/" + conceptoPago.getIdConcepto();
            } else {
                return "redirect:/conceptospago/nuevo";
            }
        }
        return "redirect:/conceptospago/listar"; // Redirige a la lista después de guardar
    }

    /**
     * Muestra el formulario para editar un concepto de pago existente.
     * GET /conceptospago/editar/{id}
     * @param id El ID del concepto de pago a editar.
     * @param model Objeto Model para pasar el ConceptoPago a la vista y listas para selects.
     * @param redirectAttributes Para añadir mensajes flash si el concepto de pago no se encuentra.
     * @return El nombre de la vista Thymeleaf (conceptospago/form.html) o una redirección.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ConceptoPago> conceptoPago = conceptoPagoService.getConceptoPagoById(id);
        if (conceptoPago.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Concepto de pago no encontrado.");
            return "redirect:/conceptospago/listar";
        }
        model.addAttribute("conceptoPago", conceptoPago.get());
        model.addAttribute("tiposConcepto", TipoConcepto.values());
        model.addAttribute("metodosCalculo", MetodoCalculo.values());
        return "conceptospago/form";
    }

    /**
     * Elimina un concepto de pago.
     * GET /conceptospago/eliminar/{id}
     * Esta acción se desencadena desde el modal de confirmación en la vista listar.
     * @param id El ID del concepto de pago a eliminar.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Una redirección a la lista de conceptos de pago.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarConceptoPago(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            conceptoPagoService.deleteConceptoPago(id);
            redirectAttributes.addFlashAttribute("successMessage", "Concepto de pago eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el concepto de pago: " + e.getMessage());
        }
        return "redirect:/conceptospago/listar";
    }

    /**
     * Muestra los detalles de un concepto de pago.
     * GET /conceptospago/detalles/{id}
     * @param id El ID del concepto de pago a mostrar.
     * @param model Objeto Model para pasar el ConceptoPago a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el concepto de pago no se encuentra.
     * @return El nombre de la vista Thymeleaf (conceptospago/detalles.html) o una redirección.
     */
    @GetMapping("/detalles/{id}")
    public String verDetallesConceptoPago(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ConceptoPago> conceptoPago = conceptoPagoService.getConceptoPagoById(id);
        if (conceptoPago.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Concepto de pago no encontrado.");
            return "redirect:/conceptospago/listar";
        }
        model.addAttribute("conceptoPago", conceptoPago.get());
        return "conceptospago/detalles";
    }
}
