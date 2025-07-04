// src/main/java/com/textilima/textilima/model/Usuario.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Entidad que representa a los usuarios del sistema.
 * Mapea a la tabla `usuarios`.
 * Relaciona un usuario con un rol y, opcionalmente, con un empleado.
 */
@Entity
@Table(name = "usuarios") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Usuario { // Se usa "Usuario" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password; // Se recomienda almacenar hashes seguros de contraseñas (BCrypt)

    @Column(name = "email", unique = true, nullable = false, length = 100) // Añadido: Campo de email
    private String email;

    @Column(name = "enabled", nullable = false) // Añadido: Estado de habilitación para Spring Security
    private boolean enabled;

    @Column(name = "last_login") // Añadido: Último inicio de sesión
    private LocalDateTime lastLogin;

    @ManyToOne(fetch = FetchType.EAGER) // Muchos usuarios pueden tener un mismo rol
    @JoinColumn(name = "id_rol", nullable = false) // Columna en `usuarios` que referencia a `roles`
    private Rol rol; // Un usuario tiene UN rol. Considera ManyToMany si un usuario puede tener varios roles.

    @OneToOne(fetch = FetchType.LAZY) // Un usuario puede estar asociado a un solo empleado (y viceversa)
    @JoinColumn(name = "id_empleado", unique = true) // Columna en `usuarios` que referencia a `empleados` (puede ser nulo)
    private Empleado empleado; // Opcional: un usuario puede no estar directamente ligado a un empleado (ej. usuario admin)

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Método @PrePersist para establecer valores por defecto al crear una entidad
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        enabled = true; // Por defecto, el usuario está habilitado al crearse
    }
}
