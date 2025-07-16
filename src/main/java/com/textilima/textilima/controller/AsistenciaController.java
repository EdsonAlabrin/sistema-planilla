// src/main/java/com/textilima/textilima/controller/AsistenciaController.java
package com.textilima.textilima.controller;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

// !!! Importaciones de Logger - ¡Estas son las que faltaban! !!!
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.textilima.textilima.model.Asistencia;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Puesto;
import com.textilima.textilima.service.AsistenciaService;
import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.service.PuestoService;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    // Instancia del Logger
    private static final Logger logger = LoggerFactory.getLogger(AsistenciaController.class);

    private final AsistenciaService asistenciaService;
    private final EmpleadoService empleadoService;
    private final PuestoService puestoService;

    @Autowired
    public AsistenciaController(AsistenciaService asistenciaService, EmpleadoService empleadoService,
            PuestoService puestoService) {
        this.asistenciaService = asistenciaService;
        this.empleadoService = empleadoService; 
        this.puestoService = puestoService;
    }

    /**
     * Muestra la lista de todas las asistencias.
     * GET /asistencias/listar
     * 
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la vista Thymeleaf (asistencias/listar.html).
     */
    @GetMapping("/listar")
    public String listarAsistencias(Model model) {
        logger.info("Solicitando listar todas las asistencias.");
        List<Asistencia> asistencias = asistenciaService.getAllAsistencias();
        model.addAttribute("asistencias", asistencias);
        logger.info("Mostrando {} asistencias en la lista.", asistencias.size());
        return "asistencias/listar";
    }

    /**
     * Muestra el formulario para registrar una nueva asistencia (marcar
     * entrada/salida).
     * GET /asistencias/registro (URL)
     * Retorna la vista asistencias/registro.html
     * 
     * @param model Objeto Model para pasar la fecha actual a la vista.
     * @return El nombre de la vista Thymeleaf (asistencias/registro.html).
     */
    @GetMapping("/registro") // <-- RUTA URL DEL NAVEGADOR: /asistencias/registro
    public String mostrarFormularioRegistro(Model model) {
        logger.info("Mostrando formulario de registro de asistencia.");
        model.addAttribute("fechaActual", LocalDate.now());
        return "asistencias/registro"; // <-- NOMBRE DEL ARCHIVO HTML: registro.html
    }

    /**
     * Procesa el marcado de asistencia por DNI. Determina si es entrada o salida.
     * POST /asistencias/marcar
     * 
     * @param numeroDocumento    El número de DNI del empleado.
     * @param tipoMarca          El tipo de marca ("entrada" o "salida") enviada
     *                           desde el formulario.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Redirección a la página de marcado de asistencia con un mensaje de
     *         éxito o error.
     */
    @PostMapping("/marcar")
    public String marcarAsistencia(@RequestParam("numeroDocumento") String numeroDocumento,
            @RequestParam("tipoMarca") String tipoMarca,
            RedirectAttributes redirectAttributes) {
        logger.info("Intento de marcar asistencia para DNI: {} con tipo de marca: {}", numeroDocumento, tipoMarca);
        try {
            Empleado empleado = empleadoService.getEmpleadoByNumeroDocumento(numeroDocumento)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con el DNI: " + numeroDocumento));
            logger.debug("Empleado encontrado: {} {}", empleado.getNombres(), empleado.getApellidos());

            Puesto puesto = empleado.getPuesto();
            if (puesto == null || puesto.getJornadaLaboralHoras() == null || puesto.getHoraInicioJornada() == null
                    || puesto.getHoraFinJornada() == null) {
                logger.warn("Puesto o información de jornada incompleta para empleado: {}", empleado.getIdEmpleado());
                throw new IllegalArgumentException(
                        "Puesto no asignado al empleado o información de jornada incompleta (horas/inicio/fin). No se puede registrar asistencia.");
            }

            LocalDate hoy = LocalDate.now();
            LocalTime horaActual = LocalTime.now();
            Optional<Asistencia> asistenciaOpt = asistenciaService.getAsistenciaByEmpleadoAndFecha(empleado, hoy);
            logger.debug("Buscando asistencia existente para empleado {} en fecha {}. Encontrado: {}",
                    empleado.getIdEmpleado(), hoy, asistenciaOpt.isPresent());

            Asistencia asistencia;
            String mensajeExito;

            if (asistenciaOpt.isPresent()) {
                asistencia = asistenciaOpt.get();
                logger.debug("Asistencia existente encontrada. Hora entrada: {}, Hora salida: {}",
                        asistencia.getHoraEntrada(), asistencia.getHoraSalida());
                if ("entrada".equals(tipoMarca)) {
                    if (asistencia.getHoraEntrada() != null) {
                        logger.warn("Intento duplicado de entrada para empleado {} en fecha {}.",
                                empleado.getIdEmpleado(), hoy);
                        redirectAttributes.addFlashAttribute("errorMessage", "Ya se registró una entrada para "
                                + empleado.getNombres() + " " + empleado.getApellidos() + " hoy.");
                        return "redirect:/asistencias/registro";
                    }
                    asistencia.setHoraEntrada(horaActual);
                    mensajeExito = "¡Entrada registrada exitosamente para " + empleado.getNombres() + " "
                            + empleado.getApellidos() + "!";
                    logger.info("Entrada registrada para empleado: {}", empleado.getIdEmpleado());
                } else if ("salida".equals(tipoMarca)) {
                    if (asistencia.getHoraSalida() != null) {
                        logger.warn("Intento duplicado de salida para empleado {} en fecha {}.",
                                empleado.getIdEmpleado(), hoy);
                        redirectAttributes.addFlashAttribute("errorMessage", "Ya se registró una salida para "
                                + empleado.getNombres() + " " + empleado.getApellidos() + " hoy.");
                        return "redirect:/asistencias/registro";
                    }
                    if (asistencia.getHoraEntrada() == null) {
                        logger.warn("Intento de salida sin entrada previa para empleado {} en fecha {}.",
                                empleado.getIdEmpleado(), hoy);
                        redirectAttributes.addFlashAttribute("errorMessage",
                                "No se puede registrar salida sin una entrada previa para " + empleado.getNombres()
                                        + " " + empleado.getApellidos() + " hoy.");
                        return "redirect:/asistencias/registro";
                    }
                    asistencia.setHoraSalida(horaActual);
                    mensajeExito = "¡Salida registrada exitosamente para " + empleado.getNombres() + " "
                            + empleado.getApellidos() + "!";
                    logger.info("Salida registrada para empleado: {}", empleado.getIdEmpleado());
                } else {
                    logger.error("Tipo de marca no válido: {}", tipoMarca);
                    throw new IllegalArgumentException("Tipo de marca no válido: " + tipoMarca);
                }
            } else {
                if ("salida".equals(tipoMarca)) {
                    logger.warn("Intento de salida sin asistencia existente para empleado {} en fecha {}.",
                            empleado.getIdEmpleado(), hoy);
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "No se puede registrar salida sin una entrada previa para " + empleado.getNombres() + " "
                                    + empleado.getApellidos() + " hoy.");
                    return "redirect:/asistencias/registro";
                }
                asistencia = new Asistencia();
                asistencia.setEmpleado(empleado);
                asistencia.setFecha(hoy);
                asistencia.setHoraEntrada(horaActual);
                asistencia.setEstado(Asistencia.EstadoAsistencia.PRESENTE);
                mensajeExito = "¡Entrada registrada exitosamente para " + empleado.getNombres() + " "
                        + empleado.getApellidos() + "!";
                logger.info("Nueva asistencia (entrada) creada para empleado: {}", empleado.getIdEmpleado());
            }

            if (asistencia.getHoraEntrada() != null && asistencia.getHoraSalida() != null) {
                logger.debug("Calculando tardanza y horas extras para asistencia con entrada y salida.");
                asistencia = asistenciaService.calculateTardinessAndOvertime(asistencia, puesto);
            } else if (asistencia.getHoraEntrada() != null) {
                logger.debug("Calculando tardanza solo para entrada.");
                if (asistencia.getHoraEntrada().isAfter(puesto.getHoraInicioJornada())) {
                    long minutosTardanza = Duration.between(puesto.getHoraInicioJornada(), asistencia.getHoraEntrada())
                            .toMinutes();
                    asistencia.setMinutosTardanza((int) minutosTardanza);
                    asistencia.setEstado(Asistencia.EstadoAsistencia.TARDANZA);
                    logger.info("Empleado {} con tardanza de {} minutos.", empleado.getIdEmpleado(), minutosTardanza);
                } else {
                    asistencia.setMinutosTardanza(0);
                    asistencia.setEstado(Asistencia.EstadoAsistencia.PRESENTE);
                }
            }

            asistenciaService.saveAsistencia(asistencia);
            redirectAttributes.addFlashAttribute("successMessage", mensajeExito);
            logger.info("Asistencia guardada/actualizada con éxito para empleado: {}", empleado.getIdEmpleado());

        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al marcar asistencia: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error inesperado al procesar asistencia: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar asistencia: " + e.getMessage());
        }
        return "redirect:/asistencias/registro";
    }

    /**
     * Muestra el reporte diario de asistencias para una fecha específica.
     * GET /asistencias/diario (URL)
     * Retorna la vista asistencias/diario.html
     * 
     * @param fecha Opcional, la fecha para la cual generar el reporte. Si no se
     *              provee, usa la fecha actual.
     * @param model Objeto Model para pasar los datos a la vista.
     * @return El nombre de la vista Thymeleaf (asistencias/diario.html).
     */
    @GetMapping("/diario")
    public String mostrarReporteDiario(@RequestParam(value = "fecha", required = false) LocalDate fecha, Model model) {
        LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();
        logger.info("===== Solicitando reporte diario de asistencias para la fecha: {} =====", fechaConsulta);
        model.addAttribute("fechaConsulta", fechaConsulta);

        List<Empleado> todosLosEmpleados = empleadoService.getAllEmpleados();
        logger.debug("Cantidad de empleados obtenidos de la base de datos: {}", todosLosEmpleados.size());
        if (todosLosEmpleados.isEmpty()) {
            logger.warn("No se encontraron empleados en la base de datos. El reporte estará vacío.");
        } else {
            todosLosEmpleados.forEach(emp -> logger.debug("Empleado en la lista: ID={}, Nombre={}, DNI={}",
                    emp.getIdEmpleado(), emp.getNombres(), emp.getNumeroDocumento()));
        }

        List<Asistencia> asistencias = new java.util.ArrayList<>();
        logger.debug("Iniciando procesamiento de asistencias para {} empleados.", todosLosEmpleados.size());

        for (Empleado empleado : todosLosEmpleados) {
            Optional<Asistencia> asistenciaOpt = asistenciaService.getAsistenciaByEmpleadoAndFecha(empleado,
                    fechaConsulta);
            if (asistenciaOpt.isPresent()) {
                asistencias.add(asistenciaOpt.get());
                logger.debug("Asistencia REAL encontrada para empleado {} (DNI: {}) en {}. Estado: {}",
                        empleado.getIdEmpleado(), empleado.getNumeroDocumento(), fechaConsulta,
                        asistenciaOpt.get().getEstado());
            } else {
                Asistencia asistenciaAusente = new Asistencia();
                asistenciaAusente.setEmpleado(empleado);
                asistenciaAusente.setFecha(fechaConsulta);
                asistenciaAusente.setEstado(Asistencia.EstadoAsistencia.AUSENTE);
                asistenciaAusente.setMinutosTardanza(0);
                asistenciaAusente.setHorasExtras25(BigDecimal.ZERO);
                asistenciaAusente.setHorasExtras35(BigDecimal.ZERO);
                asistencias.add(asistenciaAusente);
                logger.debug(
                        "No se encontró asistencia para empleado {} (DNI: {}). Añadido como AUSENTE para la fecha {}.",
                        empleado.getIdEmpleado(), empleado.getNumeroDocumento(), fechaConsulta);
            }
        }

        asistencias.sort((a1, a2) -> a1.getEmpleado().getNombres().compareToIgnoreCase(a2.getEmpleado().getNombres()));

        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fechaActual", LocalDate.now());
        logger.info(
                "Reporte diario de asistencias finalizado. Se han cargado {} registros (incluyendo ausentes) para la fecha {}.",
                asistencias.size(), fechaConsulta);
        logger.debug("Lista final de asistencias para el reporte: {}", asistencias);
        return "asistencias/diario";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        Asistencia asistencia = asistenciaService.getAsistenciaById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asistencia no encontrada con ID: " + id));

        model.addAttribute("asistencia", asistencia);
        model.addAttribute("estados", Asistencia.EstadoAsistencia.values());
        return "asistencias/editar";
    }

    @PostMapping("/editar/{id}")
    public String procesarEdicion(@PathVariable Integer id,
            @RequestParam("estado") Asistencia.EstadoAsistencia estado,
            @RequestParam("justificacion") String justificacion) {
        Asistencia asistencia = asistenciaService.getAsistenciaById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asistencia no encontrada con ID: " + id));

        asistencia.setEstado(estado);
        asistencia.setJustificacion(justificacion);
        asistenciaService.saveAsistencia(asistencia);
        return "redirect:/asistencias/diario";
    }
}
