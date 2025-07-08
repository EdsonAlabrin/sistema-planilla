package com.textilima.textilima.config; // Puedes ajustar este paquete a tu estructura

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.textilima.textilima.service.impl.UserDetailsServiceImpl;

// Esta clase configura la seguridad de tu aplicación
@Configuration // Indica que esta es una clase de configuración de Spring
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true) // Habilita la seguridad a nivel de método
public class SecurityConfig {

    /**
     * Define el codificador de contraseñas que se utilizará.
     * BCryptPasswordEncoder es un codificador robusto y recomendado.
     *
     * @return una instancia de PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el UserDetailsService que Spring Security utilizará para cargar los detalles del usuario.
     * Este UserDetailsService será implementado por nosotros para interactuar con nuestra base de datos.
     *
     * @return una instancia de UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(); //retorna implementación
    }

     /**
     * Configura el proveedor de autenticación DAO, que utiliza UserDetailsService y PasswordEncoder.
     *
     * @param userDetailsService el servicio para cargar los detalles del usuario (se inyectará automáticamente)
     * @param passwordEncoder el codificador de contraseñas (se inyectará automáticamente)
     * @return una instancia de AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Aquí se definen las reglas de autorización, el formulario de login, etc.
     *
     * @param http el objeto HttpSecurity para configurar la seguridad
     * @return una instancia de SecurityFilterChain
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/images/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/index")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/nosotros")).permitAll()

                // Páginas protegidas: /dashboard y todos los módulos
                .requestMatchers(AntPathRequestMatcher.antMatcher("/dashboard")).authenticated() // Dashboard requiere autenticación
                .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasAuthority("ROL_ADMIN")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/empleados/**")).hasAnyAuthority("ROL_ADMIN", "ROL_RRHH", "ROL_EMPLEADO")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/planillas/**")).hasAnyAuthority("ROL_ADMIN", "ROL_RRHH")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/usuarios/**")).hasAuthority("ROL_ADMIN")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/roles/**")).hasAuthority("ROL_ADMIN")
                .requestMatchers(AntPathRequestMatcher.antMatcher("/puestos/**")).hasAnyAuthority("ROL_ADMIN", "ROL_RRHH") // Agregado Puestos
                .requestMatchers(AntPathRequestMatcher.antMatcher("/bancos/**")).hasAnyAuthority("ROL_ADMIN", "ROL_RRHH")   // Agregado Bancos
                .requestMatchers(AntPathRequestMatcher.antMatcher("/conceptos/**")).hasAnyAuthority("ROL_ADMIN", "ROL_RRHH") // Agregado Conceptos
                .requestMatchers(AntPathRequestMatcher.antMatcher("/asistencias/**")).hasAnyAuthority("ROL_ADMIN", "ROL_RRHH", "ROL_EMPLEADO") // Agregado Asistencias

                .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/dashboard", true) // Redirige al dashboard después de login exitoso
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}