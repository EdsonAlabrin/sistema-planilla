package com.textilima.textilima.controller;

import com.textilima.textilima.entities.Empleado;
import com.textilima.textilima.entities.Puesto; // Importa Puesto
import com.textilima.textilima.entities.Banco; // Importa Banco
import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.service.PuestoService; // Necesario para listar puestos en el formulario de empleado
import com.textilima.textilima.service.BancoService; // Necesario para listar bancos en el formulario de empleado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final PuestoService puestoService; // Inyectar PuestoService
    private final BancoService bancoService;   // Inyectar BancoService

    // Inyección de dependencias a través del constructor
    @Autowired
    public EmpleadoController(EmpleadoService empleadoService,
                              PuestoService puestoService,
                              BancoService bancoService) {
        this.empleadoService = empleadoService;
        this.puestoService = puestoService;
        this.bancoService = bancoService;
    }

    // Muestra la lista de todos los empleados
    @GetMapping("/listar")
    public String listarEmpleados(Model model) {
        List<Empleado> empleados = empleadoService.listarTodosLosEmpleados();
        model.addAttribute("empleados", empleados);
        return "empleados/listar"; // Nombre de la vista Thymeleaf (src/main/resources/templates/empleados/list.html)
    }

    // Muestra el formulario para crear un nuevo empleado
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("empleado", new Empleado());
        List<Puesto> puestos = puestoService.listarTodosLosPuestos();
        model.addAttribute("puestos", puestos);
        List<Banco> bancos = bancoService.findAll(); // Necesitarás un BancoService
        model.addAttribute("bancos", bancos);
        return "empleados/form"; // Nombre de la vista Thymeleaf (src/main/resources/templates/empleados/form.html)
    }

    // Procesa el envío del formulario para crear o actualizar un empleado
    @PostMapping("/guardar")
    public String guardarEmpleado(@ModelAttribute("empleado") Empleado empleado, RedirectAttributes redirectAttributes) {
        try {
            if (empleado.getIdEmpleado() == null) {
                empleadoService.crearEmpleado(empleado);
                redirectAttributes.addFlashAttribute("successMessage", "Empleado creado exitosamente.");
            } else {
                empleadoService.actualizarEmpleado(empleado.getIdEmpleado(), empleado);
                redirectAttributes.addFlashAttribute("successMessage", "Empleado actualizado exitosamente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el empleado: " + e.getMessage());
            return "redirect:/empleados/nuevo"; // Redirige de nuevo al formulario en caso de error
        }
        return "redirect:/empleados/listar"; // Redirige a la lista después de guardar
    }

    // Muestra el formulario para editar un empleado existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorId(id);
        if (empleado == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Empleado no encontrado.");
            return "redirect:/empleados/listar";
        }
        model.addAttribute("empleado", empleado);
        List<Puesto> puestos = puestoService.listarTodosLosPuestos(); // Para el dropdown de puestos
        model.addAttribute("puestos", puestos);
        List<Banco> bancos = bancoService.findAll(); // Para el dropdown de bancos
        model.addAttribute("bancos", bancos);
        return "empleados/form";
    }

    // Desactiva un empleado
    @GetMapping("/desactivar/{id}")
    public String desactivarEmpleado(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.desactivarEmpleado(id);
            redirectAttributes.addFlashAttribute("successMessage", "Empleado desactivado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al desactivar el empleado: " + e.getMessage());
        }
        return "redirect:/empleados/listar";
    }

    // Activa un empleado
    @GetMapping("/activar/{id}")
    public String activarEmpleado(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            empleadoService.activarEmpleado(id);
            redirectAttributes.addFlashAttribute("successMessage", "Empleado activado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al activar el empleado: " + e.getMessage());
        }
        return "redirect:/empleados/listar";
    }

    // Muestra los detalles de un empleado
    @GetMapping("/detalles/{id}")
    public String verDetallesEmpleado(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorId(id);
        if (empleado == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Empleado no encontrado.");
            return "redirect:/empleados/listar";
        }
        model.addAttribute("empleado", empleado);
        return "empleados/detalles"; // Nombre de la vista Thymeleaf (src/main/resources/templates/empleados/details.html)
    }
}