// src/main/java/com/textilima/textilima/model/Rol.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa los roles de usuario en el sistema.
 * Mapea a la tabla `roles`.
 */
@Entity
@Table(name = "roles") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Rol { // Se usa "Rol" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre_rol", nullable = false, unique = true, length = 50)
    private String nombreRol; // Ej: ROL_ADMIN, ROL_USUARIO, ROL_RRHH

    @Column(name = "description", length = 255) // Añadido: Descripción del rol
    private String description;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Constructor para facilidad, excluyendo los campos de auditoría que se generan automáticamente
    public Rol(String nombreRol, String description) {
        this.nombreRol = nombreRol;
        this.description = description;
    }
}