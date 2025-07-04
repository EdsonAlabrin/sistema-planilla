package com.textilima.textilima; // Tu paquete base

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan; // Importa ComponentScan
import org.springframework.security.crypto.password.PasswordEncoder;

import com.textilima.textilima.model.Rol;
import com.textilima.textilima.model.Usuario;
import com.textilima.textilima.repository.RolRepository;
import com.textilima.textilima.repository.UsuarioRepository;

@SpringBootApplication
@ComponentScan(basePackages = "com.textilima.textilima") // Asegúrate que esto apunta a tu paquete base
public class TextilimaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextilimaApplication.class, args);
    }


    /**
     * Bean que se ejecuta al inicio de la aplicación para crear roles y un usuario administrador por defecto.
     * Esto es útil para el desarrollo y las pruebas iniciales.
     *
     * @param rolRepository Repositorio para la entidad Rol.
     * @param usuarioRepository Repositorio para la entidad Usuario.
     * @param passwordEncoder Codificador de contraseñas para encriptar la contraseña del usuario.
     * @return Una instancia de CommandLineRunner.
     */
    @Bean
    public CommandLineRunner demoData(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Crear Roles si no existen
            Rol rolAdmin = rolRepository.findByNombreRol("ROL_ADMIN").orElseGet(() -> {
                System.out.println("Creando rol ROL_ADMIN...");
                return rolRepository.save(new Rol("ROL_ADMIN", "Administrador del sistema con acceso total."));
            });

            Rol rolRRHH = rolRepository.findByNombreRol("ROL_RRHH").orElseGet(() -> {
                System.out.println("Creando rol ROL_RRHH...");
                return rolRepository.save(new Rol("ROL_RRHH", "Usuario con permisos para gestionar empleados y planillas."));
            });

            Rol rolEmpleado = rolRepository.findByNombreRol("ROL_EMPLEADO").orElseGet(() -> {
                System.out.println("Creando rol ROL_EMPLEADO...");
                return rolRepository.save(new Rol("ROL_EMPLEADO", "Usuario con permisos limitados para ver su información."));
            });

            // 2. Crear un Usuario Administrador por defecto si no existe
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                System.out.println("Creando usuario administrador 'admin'...");
                Usuario adminUser = new Usuario();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123")); // ¡Contraseña encriptada!
                adminUser.setEmail("admin@textilima.com");
                adminUser.setEnabled(true); // Habilitado por defecto
                adminUser.setRol(rolAdmin); // Asignar el rol de administrador
                adminUser.setCreatedAt(Instant.now()); // Establecer fecha de creación
                adminUser.setLastLogin(null); // No ha iniciado sesión aún
                usuarioRepository.save(adminUser);
                System.out.println("Usuario 'admin' creado con contraseña 'admin123'.");
            } else {
                System.out.println("El usuario 'admin' ya existe.");
            }

            
            // Puedes añadir más usuarios o lógica de inicialización aquí si lo necesitas
        };
    }

}