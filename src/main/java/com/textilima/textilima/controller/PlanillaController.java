// src/main/java/com/textilima/textilima/controller/PlanillaController.java
package com.textilima.textilima.controller;

import com.textilima.textilima.model.*;
import com.textilima.textilima.model.Planilla.TipoPlanilla;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;
import com.textilima.textilima.repository.*;
import com.textilima.textilima.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth; // Necesario para YearMonth
import java.time.Month; // <-- ¡Asegúrate de que esta importación exista!
import java.time.format.TextStyle; // <-- ¡Asegúrate de que esta importación exista!
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.Locale; // <-- ¡Asegúrate de que esta importación exista!

@Controller
@RequestMapping("/planillas")
public class PlanillaController {

    // Repositorios inyectados (mantener solo si son estrictamente necesarios aquí)
    @Autowired
    private PlanillaRepository planillaRepository;
    @Autowired
    private ParametroLegalRepository parametroLegalRepository;
    @Autowired
    private DetallePlanillaRepository detallePlanillaRepository;
    @Autowired
    private MovimientoPlanillaRepository movimientoPlanillaRepository;
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    // Servicios inyectados
    @Autowired
    private PlanillaService planillaService;
    @Autowired
    private BoletaService boletaService;
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private AsistenciaService asistenciaService;

    @GetMapping
    public String redirectToPlanillasList() {
        return "redirect:/planillas/listar";
    }

    @GetMapping("/listar")
    public String listarPlanillas(Model model) {
        List<Planilla> planillas = planillaService.getAllPlanillas();
        planillas.sort(Comparator
            .comparing(Planilla::getAnio).reversed()
            .thenComparing(Planilla::getMes).reversed()
        );
        model.addAttribute("planillas", planillas);
        return "planillas/listar";
    }

    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        Planilla newPlanilla = new Planilla();
        newPlanilla.setMes(LocalDate.now().getMonthValue());
        newPlanilla.setAnio(LocalDate.now().getYear());

        model.addAttribute("planilla", newPlanilla);
        model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
        System.out.println("DEBUG (Nuevo Planilla): Objeto Planilla creado: " + newPlanilla.getIdPlanilla() + ", Mes: " + newPlanilla.getMes() + ", Año: " + newPlanilla.getAnio());
        System.out.println("DEBUG (Nuevo Planilla): Tipos de Planilla cargados: " + Arrays.asList(TipoPlanilla.values()));
        return "planillas/form";
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Planilla> planillaOptional = planillaService.getPlanillaById(id);
        if (planillaOptional.isPresent()) {
            model.addAttribute("planilla", planillaOptional.get());
            model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
            System.out.println("DEBUG (Editar Planilla): Planilla cargada: " + planillaOptional.get().getIdPlanilla() + ", Mes: " + planillaOptional.get().getMes() + ", Año: " + planillaOptional.get().getAnio());
            System.out.println("DEBUG (Editar Planilla): Tipos de Planilla cargados: " + Arrays.asList(TipoPlanilla.values()));
            return "planillas/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Planilla no encontrada.");
            System.err.println("ERROR: Planilla con ID " + id + " no encontrada para edición.");
            return "redirect:/planillas/listar";
        }
    }

    @PostMapping("/guardar")
    public String savePlanilla(@ModelAttribute @Valid Planilla planilla,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
            model.addAttribute("error", "Por favor, corrija los errores en el formulario.");
            System.out.println("DEBUG: Errores de validación al guardar planilla: " + result.getAllErrors());
            return "planillas/form";
        }

        if (planilla.getIdPlanilla() == null) {
            planilla.setFechaGenerada(LocalDate.now());
            
            LocalDate fechaBusquedaRmv = LocalDate.of(planilla.getAnio(), planilla.getMes(), 1);
            ParametroLegal rmv = parametroLegalRepository.findVigenteByCodigoAndFecha("RMV", fechaBusquedaRmv)
                                    .orElse(parametroLegalRepository.findTopByCodigoOrderByFechaInicioVigenciaDesc("RMV")
                                    .orElse(null));
            
            if (rmv == null) {
                redirectAttributes.addFlashAttribute("error", "No se encontró un Parámetro Legal (RMV) vigente para el " + planilla.getMes() + "/" + planilla.getAnio() + ". Por favor, registre uno.");
                model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
                return "planillas/form";
            }
            planilla.setParamRmv(rmv);
        } else {
             Planilla existingPlanilla = planillaRepository.findById(planilla.getIdPlanilla())
                                     .orElseThrow(() -> new RuntimeException("Planilla no encontrada para edición"));
             planilla.setFechaGenerada(existingPlanilla.getFechaGenerada());
             planilla.setParamRmv(existingPlanilla.getParamRmv());
        }

        planillaService.savePlanilla(planilla);
        redirectAttributes.addFlashAttribute("success", "Planilla guardada exitosamente!");
        System.out.println("DEBUG: Planilla guardada: " + planilla);
        return "redirect:/planillas/listar";
    }

    @PostMapping("/generar-con-calculos")
    public String generarPlanillaConCalculos(@ModelAttribute @Valid Planilla planilla,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
            model.addAttribute("error", "Por favor, corrija los errores en el formulario para generar la planilla.");
            System.out.println("DEBUG: Errores de validación al generar con cálculos: " + result.getAllErrors());
            return "planillas/form";
        }

        try {
            Planilla generatedPlanilla = planillaService.generatePlanilla(planilla.getMes(), planilla.getAnio(), planilla.getTipoPlanilla());
            redirectAttributes.addFlashAttribute("success", "Planilla de " + generatedPlanilla.getTipoPlanilla().getDisplayName() + " para " + Month.of(generatedPlanilla.getMes()).name() + "/" + generatedPlanilla.getAnio() + " generada exitosamente!");
            System.out.println("DEBUG: Planilla generada con cálculos por el servicio: " + generatedPlanilla);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar la planilla: " + e.getMessage());
            System.err.println("ERROR: Excepción al generar planilla con cálculos: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/planillas/listar";
        }
        
        return "redirect:/planillas/listar";
    }

    @GetMapping("/{id}/detalles")
    public String showPlanillaDetails(@PathVariable("id") Integer idPlanilla, Model model, RedirectAttributes redirectAttributes) {
        try {
            Planilla planilla = planillaService.getPlanillaWithDetails(idPlanilla);
            
            planilla.getDetallesPlanilla().forEach(dp -> {
                Optional<Boleta> boletaExistente = boletaService.getBoletaByDetallePlanilla(dp);
                dp.setBoleta(boletaExistente.orElse(null)); 
            });

            model.addAttribute("planilla", planilla);
            model.addAttribute("detallesPlanilla", planilla.getDetallesPlanilla());
            return "planillas/planillaDetalles";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Planilla no encontrada: " + e.getMessage());
            System.err.println("ERROR: Planilla con ID " + idPlanilla + " no encontrada para detalles: " + e.getMessage());
            return "redirect:/planillas/listar";
        }
    }

    @PostMapping("/eliminar")
    public String deletePlanilla(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: Intentando eliminar planilla con ID: " + id);
        try {
            planillaService.eliminarPlanillaCompleta(id);
            redirectAttributes.addFlashAttribute("success", "Planilla eliminada exitosamente!");
            System.out.println("DEBUG: Planilla con ID " + id + " eliminada exitosamente por el servicio.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la planilla: " + e.getMessage());
            System.err.println("ERROR: Excepción al eliminar planilla con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/planillas/listar";
    }

    @GetMapping("/empleados/detalles-planilla/{id}")
    public String showDetalleEmpleado(@PathVariable("id") Integer idDetalle, Model model, RedirectAttributes redirectAttributes) {
        Optional<DetallePlanilla> detalleOptional = detallePlanillaRepository.findById(idDetalle);
        if (detalleOptional.isPresent()) {
            DetallePlanilla detalle = detalleOptional.get();
            
            if (detalle.getMovimientosPlanilla() == null) {
                detalle.setMovimientosPlanilla(movimientoPlanillaRepository.findByDetallePlanillaId(detalle.getIdDetalle()));
            }
            
            Empleado empleado = detalle.getEmpleado();
            Planilla planilla = detalle.getPlanilla();

            LocalDate fechaInicioMes = LocalDate.of(planilla.getAnio(), planilla.getMes(), 1);
            LocalDate fechaFinMes = fechaInicioMes.withDayOfMonth(fechaInicioMes.lengthOfMonth());
            
            List<Asistencia> asistenciasDelMes = asistenciaService.getAsistenciasByEmpleadoAndFechaBetween(empleado, fechaInicioMes, fechaFinMes);

            int totalMinutosTardanza = asistenciasDelMes.stream()
                                             .filter(a -> a.getMinutosTardanza() != null)
                                             .mapToInt(Asistencia::getMinutosTardanza)
                                             .sum();
            BigDecimal totalHorasExtras25 = asistenciasDelMes.stream()
                                                  .filter(a -> a.getHorasExtras25() != null)
                                                  .map(Asistencia::getHorasExtras25)
                                                  .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalHorasExtras35 = asistenciasDelMes.stream()
                                                  .filter(a -> a.getHorasExtras35() != null)
                                                  .map(Asistencia::getHorasExtras35)
                                                  .reduce(BigDecimal.ZERO, BigDecimal::add);
            long diasAusentes = asistenciasDelMes.stream()
                                                  .filter(a -> a.getEstado() == Asistencia.EstadoAsistencia.AUSENTE)
                                                  .count();
            long diasPresentes = asistenciasDelMes.stream()
                                                   .filter(a -> a.getEstado() == Asistencia.EstadoAsistencia.PRESENTE || a.getEstado() == Asistencia.EstadoAsistencia.TARDANZA)
                                                   .count();

            LocalTime horaInicioJornada = (empleado.getPuesto() != null) ? empleado.getPuesto().getHoraInicioJornada() : LocalTime.of(8, 0);
            LocalTime horaFinJornada = (empleado.getPuesto() != null) ? empleado.getPuesto().getHoraFinJornada() : LocalTime.of(17, 0);

            model.addAttribute("detallePlanilla", detalle);
            model.addAttribute("asistencias", asistenciasDelMes);
            model.addAttribute("totalMinutosTardanza", totalMinutosTardanza);
            model.addAttribute("totalHorasExtras25", totalHorasExtras25);
            model.addAttribute("totalHorasExtras35", totalHorasExtras35);
            model.addAttribute("diasAusentes", diasAusentes);
            model.addAttribute("diasPresentes", diasPresentes);
            model.addAttribute("jornadaInicio", horaInicioJornada);
            model.addAttribute("jornadaFin", horaFinJornada);
            // CORRECCIÓN: Usar sintaxis Java estándar para Month y TextStyle
            model.addAttribute("mesReporte", Month.of(planilla.getMes()).getDisplayName(TextStyle.FULL, new Locale("es", "PE")));
            model.addAttribute("anioReporte", planilla.getAnio());


            return "planillas/empleadoDetalles";
        } else {
            redirectAttributes.addFlashAttribute("error", "Detalle de planilla no encontrado.");
            return "redirect:/planillas/listar";
        }
    }

    @PostMapping("/generar-boleta/{idDetalle}")
    public String generarBoleta(@PathVariable Integer idDetalle, RedirectAttributes redirectAttributes) {
        Optional<DetallePlanilla> detalleOptional = detallePlanillaRepository.findById(idDetalle);
        if (detalleOptional.isPresent()) {
            DetallePlanilla detalle = detalleOptional.get();

            Optional<Boleta> existingBoleta = boletaService.getBoletaByDetallePlanilla(detalle);
            if (existingBoleta.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ya existe una boleta generada para este empleado en esta planilla.");
                return "redirect:/planillas/" + detalle.getPlanilla().getIdPlanilla() + "/detalles";
            }

            Boleta nuevaBoleta = new Boleta(
                detalle, 
                LocalDate.now(), 
                detalle.getPlanilla().getMes(), 
                detalle.getPlanilla().getAnio(),
                detalle.getSueldoBruto(),
                detalle.getTotalDescuentos(),
                detalle.getSueldoNeto()
            );
            // La ruta PDF se puede generar aquí o en el servicio de boleta
            // Por ahora, establecer una ruta simulada
            nuevaBoleta.setRutaPdf("/boletas/" + detalle.getPlanilla().getAnio() + "_" + detalle.getPlanilla().getMes() + "_" + detalle.getEmpleado().getNumeroDocumento() + ".pdf");
            nuevaBoleta.setFirmada(false); // Por defecto no firmada
            nuevaBoleta.setEnviada(false); // Por defecto no enviada

            boletaService.saveBoleta(nuevaBoleta);

            redirectAttributes.addFlashAttribute("success", "Boleta generada exitosamente para " + detalle.getEmpleado().getNombres() + " " + detalle.getEmpleado().getApellidos() + ".");
            return "redirect:/planillas/" + detalle.getPlanilla().getIdPlanilla() + "/detalles";
        } else {
            redirectAttributes.addFlashAttribute("error", "Detalle de planilla no encontrado para generar boleta.");
            return "redirect:/planillas/listar";
        }
    }

    @GetMapping("/ver-boleta/{idBoleta}")
    public String verBoleta(@PathVariable Integer idBoleta, Model model, RedirectAttributes redirectAttributes) {
        Optional<Boleta> boletaOptional = boletaService.getBoletaById(idBoleta);
        if (boletaOptional.isPresent()) {
            Boleta boleta = boletaOptional.get();
            DetallePlanilla detalle = boleta.getDetallePlanilla();
            
            if (detalle.getMovimientosPlanilla() == null || detalle.getMovimientosPlanilla().isEmpty()) {
                 detalle.setMovimientosPlanilla(movimientoPlanillaRepository.findByDetallePlanillaId(detalle.getIdDetalle()));
            }
            
            model.addAttribute("boleta", boleta);
            model.addAttribute("detallePlanilla", detalle);

            Optional<Empresa> empresaOptional = empresaService.getOneCompany();
            if (empresaOptional.isPresent()) {
                model.addAttribute("empresa", empresaOptional.get());
            } else {
                model.addAttribute("empresa", null);
            }
            
            return "boletas/verBoleta";
        } else {
            redirectAttributes.addFlashAttribute("error", "Boleta no encontrada.");
            return "redirect:/planillas/listar";
        }
    }

    @PostMapping("/eliminar-boleta/{idBoleta}")
    public String eliminarBoleta(@PathVariable("idBoleta") Integer idBoleta, RedirectAttributes redirectAttributes) {
        System.out.println("DEBUG: Intentando eliminar boleta con ID: " + idBoleta);
        Optional<Boleta> boletaOptional = boletaService.getBoletaById(idBoleta);
        if (boletaOptional.isPresent()) {
            Integer idPlanilla = boletaOptional.get().getDetallePlanilla().getPlanilla().getIdPlanilla();
            try {
                boletaService.deleteBoleta(idBoleta);
                redirectAttributes.addFlashAttribute("success", "Boleta eliminada exitosamente.");
                System.out.println("DEBUG: Boleta con ID " + idBoleta + " eliminada exitosamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar la boleta: " + e.getMessage());
                System.err.println("ERROR: Excepción al eliminar boleta con ID " + idBoleta + ": " + e.getMessage());
                e.printStackTrace();
            }
            return "redirect:/planillas/" + idPlanilla + "/detalles";
        } else {
            redirectAttributes.addFlashAttribute("error", "Boleta no encontrada para eliminación.");
            System.err.println("ERROR: Boleta con ID " + idBoleta + " no encontrada para eliminación.");
            return "redirect:/planillas/listar";
        }
    }
}
