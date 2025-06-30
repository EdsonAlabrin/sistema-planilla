package com.textilima.textilima.controller;

import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.service.PuestoService; // Necesario para listar puestos en el formulario de empleado
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.service.BancoService; // Necesario para listar bancos en el formulario de empleado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final PuestoService puestoService; // Necesario para la lista desplegable de puestos
    private final BancoService bancoService;   // Necesario para la lista desplegable de bancos

    @Autowired
    public EmpleadoController(EmpleadoService empleadoService, PuestoService puestoService, BancoService bancoService) {
        this.empleadoService = empleadoService;
        this.puestoService = puestoService;
        this.bancoService = bancoService;
    }

    /**
     * Muestra la lista de todos los empleados.
     * GET /empleados/listar
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la vista Thymeleaf (empleados/listar.html).
     */
    @GetMapping("/listar")
    public String listarEmpleados(Model model) {
        List<Empleado> empleados = empleadoService.getAllEmpleados();
        model.addAttribute("empleados", empleados);
        return "empleados/listar"; // Nombre de la vista Thymeleaf
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
        // Se corrige la llamada al método en PuestoService
        model.addAttribute("puestos", puestoService.listarTodosLosPuestos()); // Para el select de puestos
        model.addAttribute("bancos", bancoService.getAllBancos());   // Para el select de bancos
        model.addAttribute("sistemasPensiones", Empleado.SistemaPensiones.values()); // Para el select de sistemas de pensiones
        return "empleados/form"; // Nombre de la vista Thymeleaf
    }

    /**
     * Procesa el envío del formulario para crear o actualizar un empleado.
     * POST /empleados/guardar
     * @param empleado El objeto Empleado enviado desde el formulario.
     * @param redirectAttributes Para añadir mensajes flash que persisten en la redirección.
     * @return Una redirección a la lista de empleados o al formulario en caso de error.
     */
    @PostMapping("/guardar")
    public String guardarEmpleado(@ModelAttribute("empleado") Empleado empleado, RedirectAttributes redirectAttributes) {
        try {
            // Spring Binding ya se encarga de las relaciones si los IDs coinciden
            // Puedes añadir lógica de validación adicional aquí si es necesario
            empleadoService.saveEmpleado(empleado); // saveEmpleado maneja creación y actualización
            redirectAttributes.addFlashAttribute("successMessage", "Empleado guardado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el empleado: " + e.getMessage());
            // En caso de error, volvemos al formulario para que el usuario corrija
            return "redirect:/empleados/nuevo"; // O a /empleados/editar/{id} si el empleado tenía ID
        }
        return "redirect:/empleados/listar"; // Redirige a la lista después de guardar
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
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Empleado> empleado = empleadoService.getEmpleadoById(id);
        if (empleado.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Empleado no encontrado.");
            return "redirect:/empleados/listar";
        }
        model.addAttribute("empleado", empleado.get());
        // Se corrige la llamada al método en PuestoService
        model.addAttribute("puestos", puestoService.listarTodosLosPuestos());
        model.addAttribute("bancos", bancoService.getAllBancos());
        model.addAttribute("sistemasPensiones", Empleado.SistemaPensiones.values());
        return "empleados/form";
    }

    /**
     * Elimina un empleado.
     * GET /empleados/eliminar/{id}
     * @param id El ID del empleado a eliminar.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Una redirección a la lista de empleados.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.deleteEmpleado(id);
            redirectAttributes.addFlashAttribute("successMessage", "Empleado eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el empleado: " + e.getMessage());
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
    public String verDetallesEmpleado(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Empleado> empleado = empleadoService.getEmpleadoById(id);
        if (empleado.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Empleado no encontrado.");
            return "redirect:/empleados/listar";
        }
        model.addAttribute("empleado", empleado.get());
        return "empleados/detalles";
    }
}