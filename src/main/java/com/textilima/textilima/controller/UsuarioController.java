package com.textilima.textilima.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.textilima.textilima.model.Usuario;
import com.textilima.textilima.repository.RolRepository;
import com.textilima.textilima.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios") // Todas las rutas de este controlador comenzarán con /usuarios
@PreAuthorize("hasRole('ROL_ADMIN')") // Solo usuarios con el rol ROL_ADMIN pueden acceder a este controlador
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository; // Para obtener la lista de roles disponibles

    @Autowired
    private PasswordEncoder passwordEncoder; // Para encriptar contraseñas

    /**
     * Muestra la lista de todos los usuarios.
     *
     * @param model El objeto Model para pasar atributos a la vista.
     * @return El nombre de la vista (listarUsuarios.html).
     */
    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/listarUsuarios";
    }

    /**
     * Muestra el formulario para crear un nuevo usuario.
     *
     * @param model El objeto Model para pasar atributos a la vista.
     * @return El nombre de la vista (usuarioForm.html).
     */
    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        List<Rol> roles = rolRepository.findAll(); // Obtener todos los roles disponibles
        model.addAttribute("roles", roles);
        model.addAttribute("isEdit", false); // Indicador para la vista
        return "usuarios/Form";
    }

    /**
     * Guarda un nuevo usuario.
     *
     * @param usuario El objeto Usuario a guardar (viene del formulario).
     * @param result El resultado de la validación del formulario.
     * @param redirectAttributes Atributos para redirigir con mensajes flash.
     * @param model El objeto Model (en caso de error para volver a mostrar el formulario).
     * @return Redirección a la lista de usuarios o de vuelta al formulario si hay errores.
     */
    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            List<Rol> roles = rolRepository.findAll();
            model.addAttribute("roles", roles);
            model.addAttribute("isEdit", false);
            return "usuarios/Form";
        }

        // Verificar si el username o email ya existen (solo para nuevos usuarios o si se cambian en edición)
        if (usuario.getIdUsuario() == null) { // Es un nuevo usuario
            if (usuarioService.findByUsername(usuario.getUsername()).isPresent()) {
                result.rejectValue("username", "error.usuario", "El nombre de usuario ya existe.");
            }
            if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {
                result.rejectValue("email", "error.usuario", "El correo electrónico ya está registrado.");
            }
        } else { // Es una edición de usuario existente
            Optional<Usuario> existingUserByUsername = usuarioService.findByUsername(usuario.getUsername());
            if (existingUserByUsername.isPresent() && !existingUserByUsername.get().getIdUsuario().equals(usuario.getIdUsuario())) {
                result.rejectValue("username", "error.usuario", "El nombre de usuario ya existe.");
            }
            Optional<Usuario> existingUserByEmail = usuarioService.findByEmail(usuario.getEmail());
            if (existingUserByEmail.isPresent() && !existingUserByEmail.get().getIdUsuario().equals(usuario.getIdUsuario())) {
                result.rejectValue("email", "error.usuario", "El correo electrónico ya está registrado.");
            }
        }

        if (result.hasErrors()) {
            List<Rol> roles = rolRepository.findAll();
            model.addAttribute("roles", roles);
            model.addAttribute("isEdit", usuario.getIdUsuario() != null);
            return "usuarios/Form";
        }

        try {
            // La encriptación de la contraseña se maneja en UsuarioService.saveUsuario()
            usuarioService.saveUsuario(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario guardado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios/listar";
    }

    /**
     * Muestra el formulario para editar un usuario existente.
     *
     * @param id El ID del usuario a editar.
     * @param model El objeto Model para pasar atributos a la vista.
     * @param redirectAttributes Atributos para redirigir con mensajes flash si el usuario no se encuentra.
     * @return El nombre de la vista (usuarioForm.html) o redirección a la lista.
     */
    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOptional = usuarioService.getUsuarioById(idUsuario);
        if (usuarioOptional.isPresent()) {
            model.addAttribute("usuario", usuarioOptional.get());
            List<Rol> roles = rolRepository.findAll();
            model.addAttribute("roles", roles);
            model.addAttribute("isEdit", true); // Indicador para la vista
            return "usuarios/usuarioForm";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios/listar";
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @param redirectAttributes Atributos para redirigir con mensajes flash.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteUsuario(idUsuario);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios/listar";
    }

    /**
     * Cambia el estado de habilitación (enabled) de un usuario.
     *
     * @param id El ID del usuario.
     * @param enabled El nuevo estado (true para habilitar, false para deshabilitar).
     * @param redirectAttributes Atributos para redirigir con mensajes flash.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/cambiarEstado")
    public String cambiarEstadoUsuario(@RequestParam Integer idUsuario,
                                       @RequestParam boolean enabled,
                                       RedirectAttributes redirectAttributes) {
        try {
            usuarioService.changeUserEnabledStatus(idUsuario, enabled);
            redirectAttributes.addFlashAttribute("success", "Estado del usuario actualizado exitosamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado del usuario: " + e.getMessage());
        }
        return "redirect:/usuarios/listar";
    }
}
