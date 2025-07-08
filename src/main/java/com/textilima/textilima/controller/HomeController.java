package com.textilima.textilima.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    /**
     * Maneja la solicitud a la raíz de la aplicación.
     * Redirige a /dashboard si el usuario está autenticado,
     * de lo contrario, muestra la página de inicio pública (index.html).
     *
     * @return String con la vista o redirección.
     */
    @GetMapping("/")
    public String redirectToLoginOrDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Verifica si el usuario está autenticado y no es "anonymousUser" (estado por defecto antes de login)
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return "redirect:/dashboard"; // Usuario autenticado, ir al dashboard
        }
        return "index"; // Usuario no autenticado, ir a la página de inicio pública
    }

    /**
     * Muestra la página de inicio pública (index.html) directamente.
     * Esto es útil si quieres un enlace explícito a la página de inicio.
     */
    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    /**
     * Muestra la página del dashboard (requiere autenticación).
     * Este es el ÚNICO método que debe mapear a /dashboard.
     */
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }
    // Puedes añadir otros métodos para páginas públicas aquí, como /conocenos
    @GetMapping("/nosotros")
    public String conocenosPage() {
        return "nosotros"; // Asegúrate de tener nosotros.html
    }
}
