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

    private final BancoService bancoService; // Inyección del servicio de Banco

    @Autowired // Constructor para inyección de dependencias
    public BancoController(BancoService bancoService) {
        this.bancoService = bancoService;
    }

    // Muestra la lista de todos los bancos
    @GetMapping ("/listar")// Mapea las solicitudes GET a /bancos
    public String listBancos(Model model) {
        List<Banco> bancos = bancoService.findAll();
        model.addAttribute("bancos", bancos); // Añade la lista de bancos al modelo para la vista
        return "bancos/listar"; // Retorna el nombre de la vista (Thymeleaf buscará en src/main/resources/templates/bancos/list.html)
    }

    // Muestra el formulario para crear un nuevo banco
    @GetMapping("/nuevo") // Mapea las solicitudes GET a /bancos/nuevo
    public String showNewBancoForm(Model model) {
        model.addAttribute("banco", new Banco()); // Crea una nueva instancia de Banco para el formulario
        model.addAttribute("pageTitle", "Crear Nuevo Banco"); // Título para la página
        return "bancos/form"; // Retorna el formulario de banco
    }

    // Muestra el formulario para editar un banco existente
    @GetMapping("/editar/{id}") // Mapea las solicitudes GET a /bancos/editar/{id}
    public String showEditBancoForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Banco> bancoOptional = bancoService.findById(id);
        if (bancoOptional.isPresent()) {
            model.addAttribute("banco", bancoOptional.get());
            model.addAttribute("pageTitle", "Editar Banco (ID: " + id + ")");
            return "bancos/form";
        } else {
            ra.addFlashAttribute("errorMessage", "Banco no encontrado con ID: " + id);
            return "redirect:/bancos"; // Redirige a la lista si el banco no existe
        }
    }

    // Guarda un banco nuevo o actualizado
    @PostMapping("/guardar") // Mapea las solicitudes POST a /bancos/guardar
    public String saveBanco(@ModelAttribute Banco banco, RedirectAttributes ra) {
        try {
            // Validación básica para evitar nombres duplicados
            Optional<Banco> existingBancoByName = bancoService.findByNombreBanco(banco.getNombreBanco());
            if (existingBancoByName.isPresent() && !existingBancoByName.get().getIdBanco().equals(banco.getIdBanco())) {
                ra.addFlashAttribute("errorMessage", "Ya existe un banco con el nombre: " + banco.getNombreBanco());
                // Redirige al formulario con los datos pre-rellenados si es una edición, o al formulario vacío si es nuevo
                return "redirect:/bancos/" + (banco.getIdBanco() != null ? "editar/" + banco.getIdBanco() : "nuevo");
            }
            // Puedes añadir una validación similar para codigoBanco si también debe ser único
            // Optional<Banco> existingBancoByCode = bancoService.findByCodigoBanco(banco.getCodigoBanco());
            // if (banco.getCodigoBanco() != null && existingBancoByCode.isPresent() && !existingBancoByCode.get().getIdBanco().equals(banco.getIdBanco())) {
            //     ra.addFlashAttribute("errorMessage", "Ya existe un banco con el código: " + banco.getCodigoBanco());
            //     return "redirect:/bancos/" + (banco.getIdBanco() != null ? "editar/" + banco.getIdBanco() : "nuevo");
            // }

            bancoService.save(banco); // Guarda el banco
            ra.addFlashAttribute("successMessage", "Banco guardado exitosamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error al guardar el banco: " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para depuración en la consola del servidor
            // Considera redirigir de nuevo al formulario si hay un error crítico
            return "redirect:/bancos/listar" + (banco.getIdBanco() != null ? "/editar/" + banco.getIdBanco() : "/nuevo");
        }
        return "redirect:/bancos/listar"; // Redirige a la lista de bancos
    }

    // Elimina un banco por su ID
    @GetMapping("/eliminar/{id}") // Mapea las solicitudes GET a /bancos/eliminar/{id}
    public String deleteBanco(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            bancoService.deleteById(id);
            ra.addFlashAttribute("successMessage", "Banco eliminado exitosamente!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error al eliminar el banco: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/bancos/listar"; // Redirige a la lista de bancos
    }

    // Muestra los detalles de un banco específico
    @GetMapping("/detalles/{id}") // Mapea las solicitudes GET a /bancos/detalles/{id}
    public String viewBancoDetails(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Banco> bancoOptional = bancoService.findById(id);
        if (bancoOptional.isPresent()) {
            model.addAttribute("banco", bancoOptional.get());
            model.addAttribute("pageTitle", "Detalles del Banco");
            return "bancos/detalles"; // Retorna la vista de detalles
        } else {
            ra.addFlashAttribute("errorMessage", "Banco no encontrado con ID: " + id);
            return "redirect:/bancos/listar";
        }
    }
}
