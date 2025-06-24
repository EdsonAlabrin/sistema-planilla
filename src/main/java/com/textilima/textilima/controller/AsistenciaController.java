package com.textilima.textilima.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.textilima.textilima.entities.Asistencia;
import com.textilima.textilima.entities.EstadoAsistencia;
import com.textilima.textilima.service.AsistenciaService;
import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.entities.Empleado;

@Controller
@RequestMapping("/asistencia")
public class AsistenciaController {
    @Autowired
    private EmpleadoService empleadoService; // Necesario para buscar empleados por DNI

    @Autowired
    private AsistenciaService asistenciaService; // Para registrar la asistencia

    
    /**
     * Muestra la página principal de registro de asistencia.
     * @param model Objeto Model para pasar atributos a la vista.
     * @return Nombre de la plantilla Thymeleaf para el registro de asistencia.
     */
    @GetMapping("/marcar")
    public String mostrarFormularioMarcar(Model model) {
        // Puedes pre-cargar la hora actual si lo necesitas, aunque el JS ya lo hace.
        model.addAttribute("horaActual", LocalTime.now().toString());
        // Añadir mensajes flash si hay alguno redirigido
        return "asistencia/registro"; // Mapea a src/main/resources/templates/asistencia/marcar.html
    }

    /**
     * Procesa la solicitud para marcar la asistencia (entrada o salida).
     * @param dni El DNI del empleado.
     * @param horaRegistro La hora registrada desde el cliente.
     * @param redirectAttributes Para añadir mensajes flash a la redirección.
     * @return Redirección a la misma página con un mensaje de éxito o error.
     */
    @PostMapping("/marcar")
    public String marcarAsistencia(
            @RequestParam("dni") String dni,
            @RequestParam("horaRegistro") String horaRegistro,
            RedirectAttributes redirectAttributes) {

        // 1. Validar el formato del DNI (si no se hizo completamente en el frontend)
        if (dni == null || !dni.matches("\\d{8}")) {
            redirectAttributes.addFlashAttribute("mensaje", "DNI inválido. Debe contener 8 dígitos numéricos.");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/asistencia/marcar";
        }

        // 2. Buscar al empleado por DNI
        Optional<Empleado> empleadoOptional = empleadoService.findByNumeroDocumento(dni);

        if (empleadoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Empleado no encontrado con el DNI proporcionado.");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/asistencia/marcar";
        }

        Empleado empleado = empleadoOptional.get();
        LocalTime horaMarcada = LocalTime.parse(horaRegistro); // Convertir String a LocalTime

        // 3. Verificar si el empleado ya tiene una asistencia registrada para hoy
        Optional<Asistencia> asistenciaHoyOptional = asistenciaService.findAsistenciaByEmpleadoAndFecha(empleado.getIdEmpleado(), java.time.LocalDate.now());

        try {
            Asistencia asistenciaActualizada;
            if (asistenciaHoyOptional.isPresent()) {
                // Si ya hay un registro hoy, intentar registrar la SALIDA
                Asistencia asistenciaExistente = asistenciaHoyOptional.get();
                if (asistenciaExistente.getHoraEntrada() != null && asistenciaExistente.getHoraSalida() == null) {
                    // Si ya marcó entrada pero no salida, registrar salida
                    asistenciaActualizada = asistenciaService.registrarSalida(empleado, horaMarcada);
                    if (asistenciaActualizada != null) {
                        redirectAttributes.addFlashAttribute("mensaje", "¡Salida registrada exitosamente para " + empleado.getNombres() + " " + empleado.getApellidos() + "!");
                        redirectAttributes.addFlashAttribute("tipo", "success");
                    } else {
                        redirectAttributes.addFlashAttribute("mensaje", "Error al registrar la salida. No se encontró registro de entrada.");
                        redirectAttributes.addFlashAttribute("tipo", "danger");
                    }
                } else if (asistenciaExistente.getHoraEntrada() != null && asistenciaExistente.getHoraSalida() != null) {
                    // Si ya marcó entrada y salida, informar
                    redirectAttributes.addFlashAttribute("mensaje", "Ya has marcado tu entrada y salida para hoy, " + empleado.getNombres() + ".");
                    redirectAttributes.addFlashAttribute("tipo", "info");
                } else {
                    // Caso para cuando el registro existe pero no hay hora de entrada (¿error?)
                    redirectAttributes.addFlashAttribute("mensaje", "Registro de asistencia incompleto. Contacte a RH.");
                    redirectAttributes.addFlashAttribute("tipo", "warning");
                }
            } else {
                // Si no hay registro hoy, registrar la ENTRADA
                asistenciaActualizada = asistenciaService.registrarEntrada(empleado, horaMarcada);
                if (asistenciaActualizada.getEstado() == EstadoAsistencia.TARDANZA) {
                     redirectAttributes.addFlashAttribute("mensaje", "¡Entrada registrada con tardanza para " + empleado.getNombres() + " " + empleado.getApellidos() + "!");
                     redirectAttributes.addFlashAttribute("tipo", "warning");
                } else {
                     redirectAttributes.addFlashAttribute("mensaje", "¡Entrada registrada exitosamente para " + empleado.getNombres() + " " + empleado.getApellidos() + "!");
                     redirectAttributes.addFlashAttribute("tipo", "success");
                }
            }
        } catch (Exception e) {
            // Manejo general de cualquier otra excepción
            redirectAttributes.addFlashAttribute("mensaje", "Ocurrió un error al procesar la asistencia: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
            e.printStackTrace(); // Imprime el stack trace para depuración en el servidor
        }

        return "redirect:/asistencia/marcar"; // Redirige de vuelta al formulario de registro
    }

    // Endpoint para obtener todas las asistencias
    @GetMapping
    public ResponseEntity<List<Asistencia>> obtenerTodasAsistencias() {
        List<Asistencia> asistencias = asistenciaService.obtenerTodasAsistencias();
        return new ResponseEntity<>(asistencias, HttpStatus.OK);
    }

    // Endpoint para obtener una asistencia por ID
    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> obtenerAsistenciaPorId(@PathVariable Integer idAsistencia) {
        return asistenciaService.obtenerAsistenciaPorId(idAsistencia)
                .map(asistencia -> new ResponseEntity<>(asistencia, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint para guardar una nueva asistencia
    @PostMapping
    public ResponseEntity<Asistencia> saveAsistencia(@RequestBody Asistencia asistencia) {
        Asistencia newAsistencia = asistenciaService.saveAsistencia(asistencia);
        return new ResponseEntity<>(newAsistencia, HttpStatus.CREATED);
    }

    // Endpoint para eliminar una asistencia por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsistencia(@PathVariable Integer id) {
        asistenciaService.deleteAsistencia(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // !!! NUEVO ENDPOINT PARA OBTENER ASISTENCIAS POR FECHA !!!
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Asistencia>> getAsistenciasByFecha(@PathVariable String fecha) {
        try {
            LocalDate parsedFecha = LocalDate.parse(fecha); // Convierte la cadena a LocalDate
            List<Asistencia> asistencias = asistenciaService.obtenerAsistenciasByFecha(parsedFecha);
            return new ResponseEntity<>(asistencias, HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de errores si la fecha no es válida o hay un problema en el servicio
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Muestra el reporte diario de asistencias para una fecha específica.
     * @param fecha La fecha para la que se desea el reporte. Si es null, usa la fecha actual.
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para el reporte diario.
     */
    @GetMapping("/diario") // Ruta para el reporte diario
    public String mostrarReporteDiario(
            @RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {

        // Si no se proporciona una fecha, usar la fecha actual de San Juan de Lurigancho
        if (fecha == null) {
            fecha = LocalDate.now(); // LocalDate.now() ya usa la zona horaria del sistema por defecto, que debería ser correcta si el servidor está configurado para -05:00.
        }

        List<Asistencia> asistencias = asistenciaService.obtenerAsistenciasByFecha(fecha);

        // Agregamos las horas trabajadas a cada objeto Asistencia o en un DTO
        // Para este ejemplo, voy a pasar un DTO simplificado o añadir la duración al modelo directamente
        // Es mejor crear un DTO si la vista necesita campos que no están en la entidad directamente.
        // Pero para simplificar y dado que el HTML usa `asist` directamente, calcularemos en la vista.
        // Ojo: El cálculo de horas trabajadas en el HTML usando #temporals.createTime(...) tiene limitaciones
        // y puede no ser preciso para diferencias grandes o negativas (e.g., entrada a las 23:00 y salida a la 01:00).
        // Una mejor práctica sería calcular un 'duration' en el backend y pasarlo.
        // Por ahora, el cálculo en Thymeleaf es solo para la diferencia de horas/minutos/segundos del mismo día.

        model.addAttribute("fechaConsulta", fecha);
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fechaActual", LocalTime.now()); // Para mostrar "Generado el..."

        return "asistencia/diario"; // Nombre del archivo HTML en src/main/resources/templates/asistencia/
    }
}
