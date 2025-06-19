package com.textilima.textilima.config; // Puedes ajustar este paquete a tu estructura

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// Esta clase configura la seguridad de tu aplicación
@Configuration // Indica que esta es una clase de configuración de Spring
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
public class SecurityConfig {

    // Define la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite el acceso a todas las rutas sin autenticación para desarrollo
                // ¡NO USAR ASÍ EN PRODUCCIÓN!
                .anyRequest().permitAll()
            )
            // Deshabilita la protección CSRF (Cross-Site Request Forgery)
            // Necesario si no manejas tokens CSRF en formularios y APIs
            // Considerar habilitar en producción con un manejo adecuado
            .csrf(AbstractHttpConfigurer::disable)
            // Deshabilita la configuración de formulario de login por defecto
            // Ya que hemos permitido todas las rutas
            .formLogin(AbstractHttpConfigurer::disable);
            // Si tuvieras autenticación basada en sesión con estados, aquí se configuraría.

        return http.build(); // Construye y devuelve la cadena de filtros de seguridad
    }
}
