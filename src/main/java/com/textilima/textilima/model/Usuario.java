// src/main/java/com/textilima/textilima/model/Usuario.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Entidad que representa a los usuarios del sistema.
 * Mapea a la tabla `usuarios`.
 * Relaciona un usuario con un rol y, opcionalmente, con un empleado.
 */
@Entity
@Table(name = "usuarios")
@Data // Genera getters, setters, toString, equals, hashCode
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
    private String password; // Se recomienda almacenar hashes seguros de contraseñas

    @ManyToOne // Muchos usuarios pueden tener un mismo rol
    @JoinColumn(name = "id_rol", nullable = false) // Columna en `usuarios` que referencia a `roles`
    private Rol rol;

    @OneToOne // Un usuario puede estar asociado a un solo empleado (y viceversa)
    @JoinColumn(name = "id_empleado", unique = true) // Columna en `usuarios` que referencia a `empleados` (puede ser nulo)
    private Empleado empleado;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se mapea la relación ManyToOne con Rol y OneToOne con Empleado.
     * - El campo 'password' debe almacenar hashes de contraseñas, no texto plano.
     * - El campo 'id_empleado' es único, asegurando que un empleado solo tenga una cuenta de usuario.
     */
}
