package com.textilima.textilima.controller; // Paquete para los controladores

import com.textilima.textilima.model.Banco;
import com.textilima.textilima.service.BancoService; // Importa la interfaz del servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller // Marca esta clase como un controlador Spring MVC
@RequestMapping("/bancos") // Mapea todas las solicitudes que comienzan con /bancos a este controlador
public class BancoController {

    private final BancoService bancoService;

    @Autowired
    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    /**
     * Muestra la lista de todos los bancos.
     * GET /bancos/listar
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la vista Thymeleaf (bancos/listar.html).
     */
    @GetMapping("/listar")
    public String listarBancos(Model model) {
        List<Banco> bancos = bancoService.getAllBancos();
        model.addAttribute("bancos", bancos);
        return "bancos/listar"; // Nombre de la vista Thymeleaf
    }

    /**
     * Muestra el formulario para crear un nuevo banco.
     * GET /bancos/nuevo
     * @param model Objeto Model para pasar un Banco vacío a la vista.
     * @return El nombre de la vista Thymeleaf (bancos/form.html).
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("banco", new Banco());
        return "bancos/form"; // Nombre de la vista Thymeleaf
    }

    /**
     * Procesa el envío del formulario para crear o actualizar un banco.
     * POST /bancos/guardar
     * @param banco El objeto Banco enviado desde el formulario.
     * @param redirectAttributes Para añadir mensajes flash que persisten en la redirección.
     * @return Una redirección a la lista de bancos o al formulario en caso de error.
     */
    @PostMapping("/guardar")
    public String guardarBanco(@ModelAttribute("banco") Banco banco, RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas de unicidad (opcional, también pueden ser manejadas por la BD o Bean Validation)
            Optional<Banco> existingBancoByName = bancoService.getBancoByNombreBanco(banco.getNombreBanco());
            if (existingBancoByName.isPresent() && (banco.getIdBanco() == null || !existingBancoByName.get().getIdBanco().equals(banco.getIdBanco()))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ya existe un banco con el nombre '" + banco.getNombreBanco() + "'.");
                return "redirect:/bancos/nuevo"; // O redirigir a editar si es un conflicto al editar
            }

            Optional<Banco> existingBancoByCode = bancoService.getBancoByCodigoBanco(banco.getCodigoBanco());
            if (existingBancoByCode.isPresent() && (banco.getIdBanco() == null || !existingBancoByCode.get().getIdBanco().equals(banco.getIdBanco()))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ya existe un banco con el código '" + banco.getCodigoBanco() + "'.");
                return "redirect:/bancos/nuevo"; // O redirigir a editar si es un conflicto al editar
            }

            bancoService.saveBanco(banco); // saveBanco maneja creación y actualización
            redirectAttributes.addFlashAttribute("successMessage", "Banco guardado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el banco: " + e.getMessage());
            // En caso de error, volvemos al formulario para que el usuario corrija
            if (banco.getIdBanco() != null) {
                return "redirect:/bancos/editar/" + banco.getIdBanco();
            } else {
                return "redirect:/bancos/nuevo";
            }
        }
        return "redirect:/bancos/listar"; // Redirige a la lista después de guardar
    }

    /**
     * Muestra el formulario para editar un banco existente.
     * GET /bancos/editar/{id}
     * @param id El ID del banco a editar.
     * @param model Objeto Model para pasar el Banco a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el banco no se encuentra.
     * @return El nombre de la vista Thymeleaf (bancos/form.html) o una redirección.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Banco> banco = bancoService.getBancoById(id);
        if (banco.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Banco no encontrado.");
            return "redirect:/bancos/listar";
        }
        model.addAttribute("banco", banco.get());
        return "bancos/form";
    }

    /**
     * Elimina un banco.
     * GET /bancos/eliminar/{id}
     * Esta acción ahora se desencadena desde el modal de confirmación en la vista listar.
     * @param id El ID del banco a eliminar.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Una redirección a la lista de bancos.
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarBanco(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            bancoService.deleteBanco(id);
            redirectAttributes.addFlashAttribute("successMessage", "Banco eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el banco: " + e.getMessage());
        }
        return "redirect:/bancos/listar";
    }

    /**
     * Muestra los detalles de un banco.
     * GET /bancos/detalles/{id}
     * @param id El ID del banco a mostrar.
     * @param model Objeto Model para pasar el Banco a la vista.
     * @param redirectAttributes Para añadir mensajes flash si el banco no se encuentra.
     * @return El nombre de la vista Thymeleaf (bancos/detalles.html) o una redirección.
     */
    @GetMapping("/detalles/{id}")
    public String verDetallesBanco(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Banco> banco = bancoService.getBancoById(id);
        if (banco.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Banco no encontrado.");
            return "redirect:/bancos/listar";
        }
        model.addAttribute("banco", banco.get());
        return "bancos/detalles";
    }
}
