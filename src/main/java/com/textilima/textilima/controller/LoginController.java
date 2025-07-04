package com.textilima.textilima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class LoginController {
    /**
     * Muestra la página de inicio de sesión.
     * Maneja los parámetros de error y logout para mostrar mensajes al usuario.
     *
     * @param error Si es true, indica que hubo un error de autenticación.
     * @param logout Si es true, indica que la sesión se cerró exitosamente.
     * @param model El objeto Model para pasar atributos a la vista.
     * @return El nombre de la vista de login (login.html).
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Nombre de usuario o contraseña inválidos.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Sesión cerrada exitosamente.");
        }
        return "login"; // Devuelve la vista login.html
    }

    /**
     * Muestra la página del dashboard (panel de control) después de un login exitoso.
     * Esta página será accesible solo para usuarios autenticados.
     *
     * @return El nombre de la vista del dashboard (dashboard.html).
     */
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // Devuelve la vista dashboard.html
    }

    /**
     * Redirige la raíz "/" a la página de inicio de sesión o al dashboard si ya está autenticado.
     * Esto es útil para que la URL base de tu aplicación funcione correctamente con Spring Security.
     */
    @GetMapping("/")
    public String redirectToLoginOrDashboard() {
        // Spring Security manejará la redirección si el usuario ya está autenticado a /dashboard
        // o lo redirigirá a /login si no lo está.
        return "redirect:/dashboard"; // Redirige a /dashboard, Spring Security interceptará si no está logueado
    }
}
