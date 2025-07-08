package com.textilima.textilima.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.textilima.textilima.model.Rol;
import com.textilima.textilima.repository.RolRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("roles")
@PreAuthorize("hasAuthority('ROL_ADMIN')")
public class RolController {
    @Autowired
    private RolRepository rolRepository;

    /**
     * Muestra la lista de todos los roles.
     *
     * @param model El objeto Model para pasar atributos a la vista.
     * @return El nombre de la vista (listar.html).
     */
    @GetMapping("/listar")
    public String listarRoles(Model model) {
        List<Rol> roles = rolRepository.findAll();
        model.addAttribute("roles", roles);
        return "roles/listar";
    }

    /**
     * Muestra el formulario para crear un nuevo rol.
     *
     * @param model El objeto Model para pasar atributos a la vista.
     * @return El nombre de la vista (rolForm.html).
     */
    @GetMapping("/nuevo")
    public String nuevoRolForm(Model model) {
        model.addAttribute("rol", new Rol());
        model.addAttribute("isEdit", false); // Indicador para la vista
        return "roles/form";
    }

    /**
     * Guarda un nuevo rol o actualiza uno existente.
     *
     * @param rol El objeto Rol a guardar (viene del formulario).
     * @param result El resultado de la validación del formulario.
     * @param redirectAttributes Atributos para redirigir con mensajes flash.
     * @param model El objeto Model (en caso de error para volver a mostrar el formulario).
     * @return Redirección a la lista de roles o de vuelta al formulario si hay errores.
     */
    @PostMapping("/guardar")
    public String guardarRol(@Valid @ModelAttribute("rol") Rol rol,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", rol.getIdRol() != null);
            return "roles/form";
        }

        // Validación de unicidad para nombreRol
        Optional<Rol> existingRol = rolRepository.findByNombreRol(rol.getNombreRol());
        if (existingRol.isPresent() && (rol.getIdRol() == null || !existingRol.get().getIdRol().equals(rol.getIdRol()))) {
            result.rejectValue("nombreRol", "error.rol", "El nombre del rol ya existe.");
            model.addAttribute("isEdit", rol.getIdRol() != null);
            return "roles/form";
        }

        try {
            rolRepository.save(rol);
            redirectAttributes.addFlashAttribute("success", "Rol guardado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el rol: " + e.getMessage());
        }
        return "redirect:/roles/listar";
    }

    /**
     * Muestra el formulario para editar un rol existente.
     *
     * @param id El ID del rol a editar.
     * @param model El objeto Model para pasar atributos a la vista.
     * @param redirectAttributes Atributos para redirigir con mensajes flash si el rol no se encuentra.
     * @return El nombre de la vista (rolForm.html) o redirección a la lista.
     */
    @GetMapping("/editar/{id}")
    public String editarRolForm(@PathVariable("id") Integer idRol, Model model, RedirectAttributes redirectAttributes) {
        Optional<Rol> rolOptional = rolRepository.findById(idRol);
        if (rolOptional.isPresent()) {
            model.addAttribute("rol", rolOptional.get());
            model.addAttribute("isEdit", true); // Indicador para la vista
            return "roles/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Rol no encontrado.");
            return "redirect:/roles/listar";
        }
    }

    /**
     * Elimina un rol por su ID.
     *
     * @param id El ID del rol a eliminar.
     * @param redirectAttributes Atributos para redirigir con mensajes flash.
     * @return Redirección a la lista de roles.
     */
    @PostMapping("/eliminar")
    public String eliminarRol(@RequestParam Integer idRol, RedirectAttributes redirectAttributes) {
        try {
            // Antes de eliminar un rol, es crucial verificar si está asignado a algún usuario.
            // Si hay usuarios con este rol, la eliminación fallaría o dejaría usuarios sin rol.
            // Aquí puedes añadir lógica para:
            // 1. Advertir al usuario y no permitir la eliminación.
            // 2. Reasignar los usuarios a un rol por defecto (ej. ROL_USUARIO).
            // 3. Eliminar los usuarios asociados (¡no recomendado!).

            // Por ahora, simplemente intentamos eliminar y capturamos la excepción si hay una restricción de clave foránea.
            rolRepository.deleteById(idRol);
            redirectAttributes.addFlashAttribute("success", "Rol eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el rol: " + e.getMessage() + ". Asegúrate de que no esté asignado a ningún usuario.");
        }
        return "redirect:/roles/listar";
    }
}
