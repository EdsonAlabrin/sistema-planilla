package com.textilima.textilima.controller; // Asegúrate de que este paquete sea correcto

import com.textilima.textilima.entities.Planilla;
import com.textilima.textilima.service.PlanillaService;
import com.textilima.textilima.service.EmpleadoService; // Importa EmpleadoService
import com.textilima.textilima.service.ConceptoPagoService; // Importa ConceptoPagoService

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/planillas")
public class PlanillaController {

    private final PlanillaService planillaService;
    private final EmpleadoService empleadoService; // Inyecta el servicio de Empleados
    private final ConceptoPagoService conceptoPagoService; // Inyecta el servicio de Conceptos de Pago

    @Autowired
    public PlanillaController(PlanillaService planillaService, EmpleadoService empleadoService, ConceptoPagoService conceptoPagoService) {
        this.planillaService = planillaService;
        this.empleadoService = empleadoService;
        this.conceptoPagoService = conceptoPagoService;
    }

    // Muestra la lista de todas las planillas
    @GetMapping
    public String listPlanillas(Model model) {
        List<Planilla> planillas = planillaService.getAllPlanillas();
        model.addAttribute("planillas", planillas);
        model.addAttribute("pageTitle", "Listado de Planillas");
        return "planillas/listar"; // Vista src/main/resources/templates/planillas/list.html
    }

    // Muestra un formulario para generar una nueva planilla
    @GetMapping("/generar")
    public String showGenerarPlanillaForm(Model model) {
        model.addAttribute("mesActual", LocalDate.now().getMonthValue());
        model.addAttribute("anioActual", LocalDate.now().getYear());
        model.addAttribute("pageTitle", "Generar Nueva Planilla");
        // No se necesita pasar "empleados" o "conceptos" directamente al formulario
        // a menos que los uses en selects para la generación inicial.
        // La lógica de generación se basa en los empleados activos en el servicio.
        return "planillas/form"; // Vista: src/main/resources/templates/planillas/generar_form.html
    }

    // Procesa la generación de la planilla
    @PostMapping("/generar")
    public String generarPlanilla(@RequestParam Integer mes,
                                  @RequestParam Integer anio,
                                  RedirectAttributes ra) {
        try {
            Planilla nuevaPlanilla = planillaService.generarPlanillaMensual(mes, anio);
            ra.addFlashAttribute("successMessage",
                    "Planilla para " + mes + "/" + anio + " generada exitosamente con ID: " + nuevaPlanilla.getIdPlanilla());
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage",
                    "Error inesperado al generar la planilla: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
        return "redirect:/planillas";
    }

    // Muestra los detalles de una planilla específica
    @GetMapping("/detalles/{id}")
    public String viewPlanillaDetails(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Planilla> planillaOptional = planillaService.getPlanillaById(id);
        if (planillaOptional.isPresent()) {
            model.addAttribute("planilla", planillaOptional.get());
            model.addAttribute("pageTitle", "Detalles de la Planilla");
            return "planillas/detalle"; // Vista: src/main/resources/templates/planillas/details.html
        } else {
            ra.addFlashAttribute("errorMessage", "Planilla no encontrada con ID: " + id);
            return "redirect:/planillas";
        }
    }

    // Elimina una planilla
    @GetMapping("/eliminar/{id}")
    public String deletePlanilla(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            planillaService.deleteById(id);
            ra.addFlashAttribute("successMessage", "Planilla eliminada exitosamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error al eliminar la planilla: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/planillas";
    }
}
