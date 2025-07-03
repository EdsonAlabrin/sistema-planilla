// src/main/java/com/textilima/textilima/controller/EmpleadoController.java
package com.textilima.textilima.controller;

import com.textilima.textilima.model.*;
import com.textilima.textilima.model.Asistencia.EstadoAsistencia; // Importar EstadoAsistencia
import com.textilima.textilima.repository.*;
import com.textilima.textilima.service.EmpresaService;
import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.service.PuestoService; // Necesario para listar puestos en el formulario de empleado
import com.textilima.textilima.service.BancoService; // Necesario para listar bancos en el formulario de empleado
import com.textilima.textilima.service.HistorialPuestoService; // Necesario para el getPuestoByEmpleadoAndDate

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid; // Usar jakarta.validation.Valid
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Arrays; // Para TipoPlanilla

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final PuestoService puestoService;
    private final BancoService bancoService;
    private final HistorialPuestoService historialPuestoService;
    private final EmpresaService empresaService; // Inyectado

    // Repositorios adicionales necesarios para operaciones específicas en este controlador
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    @Autowired
    private PlanillaRepository planillaRepository;
    @Autowired
    private DetallePlanillaRepository detallePlanillaRepository;
    @Autowired
    private MovimientoPlanillaRepository movimientoPlanillaRepository;
    @Autowired
    private ParametroLegalRepository parametroLegalRepository;
    @Autowired
    private ConceptoPagoRepository conceptoPagoRepository;

    @Autowired // Constructor para inyección de dependencias (preferido sobre @Autowired en campos)
    public EmpleadoController(EmpleadoService empleadoService, PuestoService puestoService, BancoService bancoService, HistorialPuestoService historialPuestoService, EmpresaService empresaService) {
        this.empleadoService = empleadoService;
        this.puestoService = puestoService;
        this.bancoService = bancoService;
        this.historialPuestoService = historialPuestoService;
        this.empresaService = empresaService;
    }

    @GetMapping
    public String redirectToEmpleadosList() {
        return "redirect:/empleados/listar";
    }

    /**
     * Muestra la lista de todos los empleados, con opción de búsqueda.
     * GET /empleados/listar
     * @param query Cadena de búsqueda para DNI, nombres o apellidos.
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la vista Thymeleaf (empleados/listar.html).
     */
    @GetMapping("/listar")
    public String listarEmpleados(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Empleado> empleados;
        if (query != null && !query.trim().isEmpty()) {
            empleados = empleadoService.searchEmpleados(query);
        } else {
            empleados = empleadoService.getAllEmpleados();
        }
        model.addAttribute("empleados", empleados);
        model.addAttribute("query", query); // Para mantener el valor en el campo de búsqueda
        return "empleados/listar";
    }

    /**
     * Muestra el formulario para crear un nuevo empleado.
     * GET /empleados/nuevo
     * @param model Objeto Model para pasar un Empleado vacío a la vista y listas para selects.
     * @return El nombre de la vista Thymeleaf (empleados/form.html).
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("puestos", puestoService.listarTodosLosPuestos());
        model.addAttribute("bancos", bancoService.getAllBancos());
        model.addAttribute("sistemasPensiones", Empleado.SistemaPensiones.values());
        return "empleados/form";
    }

    /**
     * Procesa el envío del formulario para crear o actualizar un empleado.
     * POST /empleados/guardar
     * @param empleado El objeto Empleado enviado desde el formulario.
     * @param result Objeto BindingResult para manejar errores de validación.
     * @param redirectAttributes Para añadir mensajes flash que persisten en la redirección.
     * @param model Objeto Model para pasar datos de vuelta al formulario en caso de error.
     * @return Una redirección a la lista de empleados o al formulario en caso de error.
     */
    @PostMapping("/guardar")
    public String guardarEmpleado(@Valid @ModelAttribute("empleado") Empleado empleado, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("puestos", puestoService.listarTodosLosPuestos());
            model.addAttribute("bancos", bancoService.getAllBancos());
            model.addAttribute("sistemasPensiones", Empleado.SistemaPensiones.values());
            return "empleados/form";
        }
        try {
            // Verificar DNI duplicado solo si es un nuevo empleado o si el DNI ha cambiado
            if (empleado.getIdEmpleado() == null || empleadoService.getEmpleadoById(empleado.getIdEmpleado()).map(e -> !e.getNumeroDocumento().equals(empleado.getNumeroDocumento())).orElse(true)) {
                if (empleadoService.getEmpleadoByNumeroDocumento(empleado.getNumeroDocumento()).isPresent()) {
                    result.rejectValue("numeroDocumento", "error.empleado", "El DNI ya está registrado.");
                    model.addAttribute("puestos", puestoService.listarTodosLosPuestos());
                    model.addAttribute("bancos", bancoService.getAllBancos());
                    model.addAttribute("sistemasPensiones", Empleado.SistemaPensiones.values());
                    return "empleados/form";
                }
            }
            // Delega la lógica de guardado y gestión de historial al servicio
            empleadoService.saveEmpleadoAndManagePuestoHistory(empleado);
            redirectAttributes.addFlashAttribute("success", "Empleado guardado exitosamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el empleado: " + e.getMessage());
            System.err.println("ERROR: Excepción al guardar empleado: " + e.getMessage());
            e.printStackTrace();
            // Si hay un error, volver al formulario con los datos y el mensaje de error
            model.addAttribute("empleado", empleado);
            model.addAttribute("puestos", puestoService.listarTodosLosPuestos());
            model.addAttribute("bancos", bancoService.getAllBancos());
            model.addAttribute("sistemasPensiones", Empleado.SistemaPensiones.values());
            return "empleados/form";
        }
        return "redirect:/empleados/listar";
    }

    /**
     * Muestra el formulario para editar un empleado existente.
     * GET /empleados/editar/{id}
     * @param id El ID del empleado a editar.
     * @param model Objeto Model para pasar el Empleado a la vista y listas para selects.
     * @param redirectAttributes Para añadir mensajes flash si el empleado no se encuentra.
     * @return El nombre de la vista Thymeleaf (empleados/form.html) o una redirección.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer idEmpleado, Model model, RedirectAttributes redirectAttributes) {
        Optional<Empleado> empleado = empleadoService.getEmpleadoById(idEmpleado);
        if (empleado.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/empleados/listar";
        }
        model.addAttribute("empleado", empleado.get());
        model.addAttribute("puestos", puestoService.listarTodosLosPuestos());
        model.addAttribute("bancos", bancoService.getAllBancos());
        model.addAttribute("sistemasPensiones", Empleado.SistemaPensiones.values());
        return "empleados/form";
    }

    /**
     * Elimina un empleado.
     * POST /empleados/eliminar (cambiado a POST para seguridad)
     * @param idEmpleado El ID del empleado a eliminar.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Una redirección a la lista de empleados.
     */
    @PostMapping("/eliminar")
    public String eliminarEmpleado(@RequestParam("id") Integer idEmpleado, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.deleteEmpleado(idEmpleado);
            redirectAttributes.addFlashAttribute("success", "Empleado eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el empleado: " + e.getMessage());
        }
        return "redirect:/empleados/listar";
    }

    /**
     * Muestra los detalles de un empleado.
     * GET /empleados/detalles/{id}
     * @param id El ID del empleado a mostrar.
     * @param model Objeto Model para pasar el Empleado a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el empleado no se encuentra.
     * @return El nombre de la vista Thymeleaf (empleados/detalles.html) o una redirección.
     */
    @GetMapping("/detalles/{id}")
    public String verDetallesEmpleado(@PathVariable("id") Integer idEmpleado, Model model, RedirectAttributes redirectAttributes) {
        Optional<Empleado> empleado = empleadoService.getEmpleadoById(idEmpleado);
        if (empleado.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/empleados/listar";
        }
        model.addAttribute("empleado", empleado.get());
        return "empleados/detalles"; // Asume que tienes una plantilla empleados/detalles.html
    }

    /**
     * Cambia el estado de un empleado (activo/inactivo).
     * POST /empleados/cambiarEstado
     * @param id El ID del empleado.
     * @param nuevoEstado El nuevo estado (true para activo, false para inactivo).
     * @param redirectAttributes Para mensajes flash.
     * @return Redirección a la lista de empleados.
     */
    @PostMapping("/cambiarEstado")
    public String cambiarEstadoEmpleado(@RequestParam("id") Integer id, @RequestParam("estado") Boolean nuevoEstado, RedirectAttributes redirectAttributes) {
        Optional<Empleado> empleadoOptional = empleadoService.getEmpleadoById(id);
        if (empleadoOptional.isPresent()) {
            Empleado empleado = empleadoOptional.get();
            empleado.setEstado(nuevoEstado);
            empleadoService.saveEmpleadoAndManagePuestoHistory(empleado); // Usar el servicio que gestiona el historial
            redirectAttributes.addFlashAttribute("success", "Estado del empleado " + empleado.getApellidos() + ", " + empleado.getNombres() + " actualizado a " + (nuevoEstado ? "Activo" : "Inactivo") + ".");
        } else {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado para cambiar estado.");
        }
        return "redirect:/empleados/listar";
    }

    // --- Métodos para Reportes Mensuales de Empleados ---
    // Método para mostrar el formulario de reporte mensual por empleado
    @GetMapping("/reporte-mensual")
    public String showReporteMensualForm(Model model) {
        model.addAttribute("empleados", empleadoService.getAllEmpleados()); // Usar el servicio
        model.addAttribute("selectedEmpleadoId", null);
        model.addAttribute("selectedMes", LocalDate.now().getMonthValue());
        model.addAttribute("selectedAnio", LocalDate.now().getYear());
        return "empleados/reporteMensualForm";
    }

    // Método para generar y mostrar el reporte mensual
    @PostMapping("/generar-reporte-mensual")
    public String generarReporteMensual(@RequestParam("empleadoId") Integer idEmpleado,
                                        @RequestParam("mes") Integer mes,
                                        @RequestParam("anio") Integer anio,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        
        Optional<Empleado> empleadoOptional = empleadoService.getEmpleadoById(idEmpleado); // Usar el servicio
        if (!empleadoOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Empleado no encontrado.");
            return "redirect:/empleados/reporte-mensual";
        }
        Empleado empleado = empleadoOptional.get();

        // Buscar si ya existe una planilla generada para este empleado en el mes/año
        // Se asume que PlanillaRepository, DetallePlanillaRepository, MovimientoPlanillaRepository están inyectados
        Optional<Planilla> planillaOptional = planillaRepository.findByMesAndAnio(mes, anio);
        DetallePlanilla detallePlanilla = null;
        List<MovimientoPlanilla> movimientosPlanilla = new ArrayList<>();

        if (planillaOptional.isPresent()) {
            Planilla planilla = planillaOptional.get();
            Optional<DetallePlanilla> dpOptional = detallePlanillaRepository.findByPlanillaAndEmpleado(planilla, empleado);
            if (dpOptional.isPresent()) {
                detallePlanilla = dpOptional.get();
                movimientosPlanilla = movimientoPlanillaRepository.findByDetallePlanilla(detallePlanilla);
                System.out.println("DEBUG: Reporte para " + empleado.getNombres() + " " + empleado.getApellidos() + " cargado desde planilla existente.");
            } else {
                System.out.println("DEBUG: No se encontró DetallePlanilla para el empleado en la planilla existente. Se calculará asistencia en vivo.");
            }
        } else {
            System.out.println("DEBUG: No se encontró planilla para " + mes + "/" + anio + ". Se calculará asistencia en vivo.");
        }

        // Recalculamos los datos de asistencia para el reporte (siempre para asegurar consistencia)
        LocalDate fechaInicioMes = LocalDate.of(anio, mes, 1);
        LocalDate fechaFinMes = fechaInicioMes.withDayOfMonth(fechaInicioMes.lengthOfMonth());
        List<Asistencia> asistenciasDelMes = asistenciaRepository.findByEmpleadoAndFechaBetween(empleado, fechaInicioMes, fechaFinMes);

        // Obtener la jornada laboral del puesto del empleado para el periodo del reporte
        Optional<HistorialPuesto> puestoHistoricoOpt = historialPuestoService.getPuestoByEmpleadoAndDate(empleado, fechaFinMes);
        Puesto puestoReporte = puestoHistoricoOpt.map(HistorialPuesto::getPuesto).orElse(null);

        LocalTime horaInicioJornada = null;
        LocalTime horaFinJornada = null;
        if (puestoReporte != null) {
            horaInicioJornada = puestoReporte.getHoraInicioJornada();
            horaFinJornada = puestoReporte.getHoraFinJornada();
        }

        // Sumar los totales de los campos ya calculados en las asistencias (para el resumen)
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
                                              .filter(a -> a.getEstado() == EstadoAsistencia.AUSENTE)
                                              .count();
        long diasPresentes = asistenciasDelMes.stream()
                                               .filter(a -> a.getEstado() == EstadoAsistencia.PRESENTE || a.getEstado() == EstadoAsistencia.TARDANZA)
                                               .count();


        // Preparar datos para la vista
        model.addAttribute("empleado", empleado);
        model.addAttribute("mesReporte", Month.of(mes).getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
        model.addAttribute("anioReporte", anio);
        model.addAttribute("asistencias", asistenciasDelMes);
        model.addAttribute("totalMinutosTardanza", totalMinutosTardanza);
        model.addAttribute("totalHorasExtras25", totalHorasExtras25);
        model.addAttribute("totalHorasExtras35", totalHorasExtras35);
        model.addAttribute("diasAusentes", diasAusentes);
        model.addAttribute("diasPresentes", diasPresentes);
        model.addAttribute("jornadaInicio", horaInicioJornada);
        model.addAttribute("jornadaFin", horaFinJornada);

        // Si existe DetallePlanilla, usar sus valores para los totales
        if (detallePlanilla != null) {
            model.addAttribute("detallePlanilla", detallePlanilla);
            model.addAttribute("movimientosPlanilla", movimientosPlanilla);
            model.addAttribute("reporteGeneradoDePlanilla", true);
        } else {
            model.addAttribute("reporteGeneradoDePlanilla", false);
            // Si no hay DetallePlanilla, podemos mostrar cálculos estimados o indicar que no hay planilla generada
            // Usamos el puesto del historial para el sueldo base estimado
            BigDecimal sueldoBaseEstimado = (puestoReporte != null) ? puestoReporte.getSalarioBase() : BigDecimal.ZERO;
            model.addAttribute("sueldoBaseEstimado", sueldoBaseEstimado);
            
            // Asignación Familiar Estimada (si aplica)
            BigDecimal rmvValor = BigDecimal.ZERO;
            Optional<ParametroLegal> rmvOptional = parametroLegalRepository.findVigenteByCodigoAndFecha("RMV", fechaFinMes);
            if (rmvOptional.isPresent()) {
                rmvValor = rmvOptional.get().getValor();
            }
            BigDecimal asignacionFamiliarEstimado = BigDecimal.ZERO;
            if (empleado.getTieneHijosCalificados() != null && empleado.getTieneHijosCalificados() && rmvValor.compareTo(BigDecimal.ZERO) > 0) {
                asignacionFamiliarEstimado = rmvValor.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            }
            model.addAttribute("asignacionFamiliarEstimado", asignacionFamiliarEstimado);
        }

        // Datos de la empresa para el encabezado del reporte
        Optional<Empresa> empresaOptional = empresaService.getOneCompany();
        empresaOptional.ifPresent(empresa -> model.addAttribute("empresa", empresa));

        return "empleados/reporteMensual";
    }
}
