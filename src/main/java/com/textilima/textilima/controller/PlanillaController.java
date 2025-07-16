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
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime; // Necesario para YearMonth
import java.time.Month; // <-- ¡Asegúrate de que esta importación exista!
import java.time.format.TextStyle; // <-- ¡Asegúrate de que esta importación exista!
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    // Servicios inyectados
    @Autowired
    private PlanillaService planillaService;
    @Autowired
    private BoletaService boletaService;
    @Autowired
    private EmpresaService empresaService;
    @Autowired
    private AsistenciaService asistenciaService;
    @Autowired
    private ConceptoPagoService conceptoPagoService;

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
        if (boletaOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Boleta no encontrada.");
            return "redirect:/planillas/listar";
        }
        Boleta boleta = boletaOptional.get();
        if (boleta.getDetallePlanilla() == null) {
            redirectAttributes.addFlashAttribute("error", "Error: La boleta no tiene un detalle de planilla asociado.");
            return "redirect:/planillas/listar";
        }
        Integer idDetalle = boleta.getDetallePlanilla().getIdDetalle();

        Optional<DetallePlanilla> loadedDetalleOptional = detallePlanillaRepository.findByIdWithMovimientos(idDetalle);

        if (loadedDetalleOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Error: El detalle de planilla (con movimientos) no pudo ser cargado.");
            return "redirect:/planillas/listar";
        }
        DetallePlanilla detalle = loadedDetalleOptional.get();

        if (detalle.getPlanilla().getTipoPlanilla() == TipoPlanilla.CTS) {
            int mes = detalle.getPlanilla().getMes();
            int anio = detalle.getPlanilla().getAnio();
            LocalDate fechaIngreso = detalle.getEmpleado().getFechaIngreso();
            BigDecimal sueldoBase = detalle.getSueldoBase();
            BigDecimal asignacionFamiliar = detalle.getAsignacionFamiliar();
            if(asignacionFamiliar == null) asignacionFamiliar = BigDecimal.ZERO;

            if (mes != 5 && mes != 11) {
                redirectAttributes.addFlashAttribute("error", "Error interno: Planilla de CTS con mes inválido.");
                return "redirect:/planillas/listar";
            }
            
            BigDecimal sextaParteGratificacion = BigDecimal.ZERO; 
            int mesGratificacionAnterior = (mes == 5) ? 12 : 7; // Si es CTS de Mayo, busca Dic del año anterior; si es Nov, busca Julio
            int anioGratificacionAnterior = (mes == 5) ? anio - 1 : anio; // Año para la gratificación a buscar

            // Buscar la gratificación pagada en el semestre anterior (que afectará la CTS actual)
            Optional<MovimientoPlanilla> gratificacionMovOpt = movimientoPlanillaRepository.findGratificacionByEmpleadoAndPeriodoAndConcepto(
                detalle.getEmpleado().getIdEmpleado(), mesGratificacionAnterior, anioGratificacionAnterior, "Gratificación Ordinaria");

            if(gratificacionMovOpt.isPresent()){
                BigDecimal montoGratificacion = gratificacionMovOpt.get().getMonto();
                sextaParteGratificacion = montoGratificacion.divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP);
            }
            // FIN CÁLCULO SEXTA PARTE GRATIFICACIÓN

            BigDecimal remuneracionComputableCts = sueldoBase.add(asignacionFamiliar).add(sextaParteGratificacion);

            LocalDate inicioPeriodoCTS;
            LocalDate finPeriodoCTS;

            if (mes == 5) { // Depósito de Mayo: Periodo Noviembre-Abril
                inicioPeriodoCTS = LocalDate.of(anio - 1, 11, 1);
                finPeriodoCTS = LocalDate.of(anio, 4, 30);
            } else { // mes == 11 (Depósito de Noviembre: Periodo Mayo-Octubre)
                inicioPeriodoCTS = LocalDate.of(anio, 5, 1);
                finPeriodoCTS = LocalDate.of(anio, 10, 31);
            }
            
            if (fechaIngreso.isAfter(inicioPeriodoCTS)) {
                inicioPeriodoCTS = fechaIngreso;
            }

            int mesesComputables = 0;
            int diasComputables = 0;
            LocalDate currentDay = inicioPeriodoCTS;
            while (!currentDay.isAfter(finPeriodoCTS)) {
                LocalDate endOfMonth = currentDay.with(TemporalAdjusters.lastDayOfMonth());
                if (endOfMonth.isAfter(finPeriodoCTS)) {
                    endOfMonth = finPeriodoCTS;
                }
                long daysWorkedInMonth = Duration.between(currentDay.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay()).toDays();

                if (daysWorkedInMonth >= 15) {
                    mesesComputables++;
                } else if (daysWorkedInMonth > 0) {
                    diasComputables += daysWorkedInMonth;
                }
                currentDay = endOfMonth.plusDays(1);
            }
            mesesComputables += diasComputables / 30;
            diasComputables = diasComputables % 30;
            
            BigDecimal montoCtsPorMes = remuneracionComputableCts.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
            BigDecimal montoCtsPorDia = remuneracionComputableCts.divide(BigDecimal.valueOf(360), 4, RoundingMode.HALF_UP);
            
            BigDecimal montoCTS = (montoCtsPorMes.multiply(BigDecimal.valueOf(mesesComputables)))
                                        .add(montoCtsPorDia.multiply(BigDecimal.valueOf(diasComputables)))
                                        .setScale(2, RoundingMode.HALF_UP);

            model.addAttribute("inicioPeriodoCTS", inicioPeriodoCTS);
            model.addAttribute("finPeriodoCTS", finPeriodoCTS);
            model.addAttribute("mesesComputables", mesesComputables);
            model.addAttribute("diasComputables", diasComputables);
            model.addAttribute("remuneracionComputableCts", remuneracionComputableCts);
            model.addAttribute("montoCtsPorMes", montoCtsPorMes);
            model.addAttribute("montoCtsPorDia", montoCtsPorDia);
            model.addAttribute("sextaParteGratificacion", sextaParteGratificacion);
            
            List<MovimientoPlanilla> ctsIngresos = new ArrayList<>();
            Optional<ConceptoPago> conceptoCtsOpt = conceptoPagoService.getConceptoPagoByNombreAndTipo("CTS", TipoConcepto.INGRESO);
            if(conceptoCtsOpt.isPresent()){
                ctsIngresos.add(new MovimientoPlanilla(null, detalle, conceptoCtsOpt.get(), montoCTS));
            } else {
                System.err.println("Concepto 'CTS' no encontrado para la boleta de CTS.");
            }
            model.addAttribute("ingresos", ctsIngresos);
            model.addAttribute("descuentos", new ArrayList<>());
            model.addAttribute("aportesEmpleador", new ArrayList<>());

            // Desactivar datos de asistencia para CTS
            model.addAttribute("asistencias", new ArrayList<>()); 
            model.addAttribute("totalMinutosTardanza", 0);
            model.addAttribute("totalHorasExtras25", BigDecimal.ZERO);
            model.addAttribute("totalHorasExtras35", BigDecimal.ZERO);
            model.addAttribute("diasAusentes", 0);
            model.addAttribute("diasPresentes", 0);
            model.addAttribute("jornadaInicio", null); 
            model.addAttribute("jornadaFin", null); 
            
            detalle.setSueldoBruto(montoCTS);
            detalle.setSueldoNeto(montoCTS);
            detalle.setTotalIngresosAdicionales(montoCTS);
            detalle.setTotalDescuentos(BigDecimal.ZERO);
            detalle.setTotalAportesEmpleador(BigDecimal.ZERO);

        } else if (detalle.getPlanilla().getTipoPlanilla() == TipoPlanilla.GRATIFICACION) {
            int mes = detalle.getPlanilla().getMes();
            int anio = detalle.getPlanilla().getAnio();
            LocalDate fechaIngreso = detalle.getEmpleado().getFechaIngreso();
            LocalDate fechaCese = detalle.getEmpleado().getFechaCese(); // Obtener fecha de cese para ajustes
            BigDecimal sueldoBase = detalle.getSueldoBase();
            
            // **Validación de Mes de Gratificación**
            if (mes != 7 && mes != 12) {
                redirectAttributes.addFlashAttribute("error", "Error interno: Planilla de Gratificación con mes inválido. Debe ser Julio o Diciembre.");
                return "redirect:/planillas/listar"; 
            }

            // **Determinación del Periodo Computable de la Gratificación (Para la vista)**
            LocalDate inicioPeriodoGratificacion;
            LocalDate finPeriodoGratificacion;

            if (mes == 7) { // Gratificación de Julio: Periodo Enero-Junio
                inicioPeriodoGratificacion = LocalDate.of(anio, 1, 1);
                finPeriodoGratificacion = LocalDate.of(anio, 6, 30);
            } else { // mes == 12 (Gratificación de Diciembre: Periodo Julio-Diciembre)
                inicioPeriodoGratificacion = LocalDate.of(anio, 7, 1);
                finPeriodoGratificacion = LocalDate.of(anio, 12, 31);
            }
            
            // **Ajustar el inicio y fin del periodo basándose en la fecha de ingreso y cese del empleado**
            LocalDate periodoRealInicioGrat = fechaIngreso.isAfter(inicioPeriodoGratificacion) ? fechaIngreso : inicioPeriodoGratificacion;
            // Considerar fecha de cese si existe y es anterior al fin del periodo teórico
            LocalDate periodoRealFinGrat = (fechaCese != null && fechaCese.isBefore(finPeriodoGratificacion)) ? fechaCese : finPeriodoGratificacion;

            // **Cálculo de Meses y Días Computables para la Vista (LÓGICA DUPLICADA DEL SERVICIO, IGUAL QUE EN CTS)**
            int mesesComputablesGratificacion = 0;
            int diasComputablesGratificacion = 0; 
            
            // Si el periodo real es inválido (ej. fecha de ingreso posterior a fecha de cese ajustada), no hay meses computables
            if (periodoRealInicioGrat.isAfter(periodoRealFinGrat)) {
                mesesComputablesGratificacion = 0;
                diasComputablesGratificacion = 0;
            } else {
                LocalDate tempDay = periodoRealInicioGrat;
                while (!tempDay.isAfter(periodoRealFinGrat)) {
                    LocalDate endOfCurrentMonth = tempDay.with(TemporalAdjusters.lastDayOfMonth());
                    LocalDate effectiveEndOfMonth = endOfCurrentMonth.isAfter(periodoRealFinGrat) ? periodoRealFinGrat : endOfCurrentMonth;

                    long daysInMonthSegment = Duration.between(tempDay.atStartOfDay(), effectiveEndOfMonth.plusDays(1).atStartOfDay()).toDays();

                    if (daysInMonthSegment >= 15) { // Si el segmento del mes tiene 15 días o más, cuenta como un mes completo.
                        mesesComputablesGratificacion++;
                    } else { // Si tiene menos de 15 días, esos días se acumulan.
                        diasComputablesGratificacion += daysInMonthSegment;
                    }
                    
                    tempDay = endOfCurrentMonth.plusDays(1); // Mover al inicio del siguiente mes
                    if (tempDay.isAfter(periodoRealFinGrat)) {
                        break;
                    }
                }
            }
            // Consolidar días sueltos en meses completos (cada 30 días es un mes para gratificación)
            mesesComputablesGratificacion += diasComputablesGratificacion / 30;
            // Los días restantes (diasComputablesGratificacion % 30) NO generan gratificación proporcional para gratificación.
            
            // Asegurar que el número de meses no sea negativo
            if (mesesComputablesGratificacion < 0) mesesComputablesGratificacion = 0;

            // **Obtener los montos de Gratificación y Bonificación Extraordinaria de los MovimientosPlanilla (Ya calculados por el servicio)**
            BigDecimal montoGratificacionBase = detalle.getMovimientosPlanilla().stream()
                .filter(mp -> "Gratificación Ordinaria".equals(mp.getConcepto().getNombreConcepto()) && mp.getConcepto().getTipo() == TipoConcepto.INGRESO)
                .map(MovimientoPlanilla::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal bonificacionExtraordinaria = detalle.getMovimientosPlanilla().stream()
                .filter(mp -> "Bonificación Extraordinaria (Ley)".equals(mp.getConcepto().getNombreConcepto()) && mp.getConcepto().getTipo() == TipoConcepto.INGRESO)
                .map(MovimientoPlanilla::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // **Remuneración Computable (Solo Sueldo Base)**
            BigDecimal remuneracionComputableGratificacion = sueldoBase; 

            // **Añadir al modelo las variables específicas de Gratificación para la vista**
            model.addAttribute("inicioPeriodoGratificacion", periodoRealInicioGrat); 
            model.addAttribute("finPeriodoGratificacion", periodoRealFinGrat);     
            model.addAttribute("mesesComputablesGratificacion", mesesComputablesGratificacion); 
            // Si quisieras mostrar también los días restantes (aunque para gratificación no generen monto):
            // model.addAttribute("diasComputablesGratificacionRestantes", diasComputablesGratificacion % 30);
            model.addAttribute("remuneracionComputableGratificacion", remuneracionComputableGratificacion); 
            model.addAttribute("montoGratificacionBase", montoGratificacionBase);
            model.addAttribute("bonificacionExtraordinaria", bonificacionExtraordinaria);

            // **Asegurarse de que los otros atributos de la boleta sean nulos o vacíos si no aplican, igual que en CTS**
            model.addAttribute("asistencias", new ArrayList<>()); 
            model.addAttribute("totalMinutosTardanza", 0);
            model.addAttribute("totalHorasExtras25", BigDecimal.ZERO);
            model.addAttribute("totalHorasExtras35", BigDecimal.ZERO);
            model.addAttribute("diasAusentes", 0);
            model.addAttribute("diasPresentes", 0);
            model.addAttribute("jornadaInicio", null); 
            model.addAttribute("jornadaFin", null); 

        } else {
            // --- Lógica existente para Planilla Mensual o Liquidación ---
            LocalDate fechaInicioMes = LocalDate.of(detalle.getPlanilla().getAnio(), detalle.getPlanilla().getMes(), 1);
            LocalDate fechaFinMes = fechaInicioMes.withDayOfMonth(fechaInicioMes.lengthOfMonth());
            List<Asistencia> asistenciasDelMes = asistenciaService.getAsistenciasByEmpleadoAndFechaBetween(detalle.getEmpleado(), fechaInicioMes, fechaFinMes);
            
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
            LocalTime horaInicioJornada = (detalle.getEmpleado().getPuesto() != null) ? detalle.getEmpleado().getPuesto().getHoraInicioJornada() : LocalTime.of(8, 0);
            LocalTime horaFinJornada = (detalle.getEmpleado().getPuesto() != null) ? detalle.getEmpleado().getPuesto().getHoraFinJornada() : LocalTime.of(17, 0);

            model.addAttribute("asistencias", asistenciasDelMes);
            model.addAttribute("totalMinutosTardanza", totalMinutosTardanza);
            model.addAttribute("totalHorasExtras25", totalHorasExtras25);
            model.addAttribute("totalHorasExtras35", totalHorasExtras35);
            model.addAttribute("diasAusentes", diasAusentes);
            model.addAttribute("diasPresentes", diasPresentes);
            model.addAttribute("jornadaInicio", horaInicioJornada);
            model.addAttribute("jornadaFin", horaFinJornada);

            // Asegúrate de que los ingresos, descuentos y aportesEmpleador se obtengan del detalle original para planillas no CTS
            // (Esta sección se ejecutará si no es CTS ni Gratificación)
            List<MovimientoPlanilla> ingresos = detalle.getMovimientosPlanilla().stream()
                                                    .filter(mp -> mp.getConcepto().getTipo() == TipoConcepto.INGRESO)
                                                    .collect(Collectors.toList());
            List<MovimientoPlanilla> descuentos = detalle.getMovimientosPlanilla().stream()
                                                        .filter(mp -> mp.getConcepto().getTipo() == TipoConcepto.DESCUENTO)
                                                        .collect(Collectors.toList());
            List<MovimientoPlanilla> aportesEmpleador = detalle.getMovimientosPlanilla().stream()
                                                                .filter(mp -> mp.getConcepto().getTipo() == TipoConcepto.APORTE_EMPLEADOR)
                                                                .collect(Collectors.toList());
            model.addAttribute("ingresos", ingresos);
            model.addAttribute("descuentos", descuentos);
            model.addAttribute("aportesEmpleador", aportesEmpleador);
        }

        List<MovimientoPlanilla> ingresosFinal = detalle.getMovimientosPlanilla().stream()
                .filter(mp -> mp.getConcepto().getTipo() == TipoConcepto.INGRESO)
                .collect(Collectors.toList());
        List<MovimientoPlanilla> descuentosFinal = detalle.getMovimientosPlanilla().stream()
                .filter(mp -> mp.getConcepto().getTipo() == TipoConcepto.DESCUENTO)
                .collect(Collectors.toList());
        List<MovimientoPlanilla> aportesEmpleadorFinal = detalle.getMovimientosPlanilla().stream()
                .filter(mp -> mp.getConcepto().getTipo() == TipoConcepto.APORTE_EMPLEADOR)
                .collect(Collectors.toList());
        

        model.addAttribute("boleta", boleta);
        model.addAttribute("detallePlanilla", detalle);
        model.addAttribute("empleado", detalle.getEmpleado());
        model.addAttribute("planillaPadre", detalle.getPlanilla());

        model.addAttribute("mesReporte", Month.of(detalle.getPlanilla().getMes()).getDisplayName(TextStyle.FULL, new Locale("es", "PE")));
        model.addAttribute("anioReporte", detalle.getPlanilla().getAnio());

        Optional<Empresa> empresaOptional = empresaService.getOneCompany();
        model.addAttribute("empresa", empresaOptional.orElse(null));

        return "boletas/verBoleta";
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
