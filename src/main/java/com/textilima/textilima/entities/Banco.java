package com.textilima.textilima.entities; // Asegúrate de que el paquete sea correcto

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Genera getters, setters, equals, hashCode, toString
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
@Entity // Marca la clase como una entidad JPA
@Table(name = "bancos") // Especifica el nombre de la tabla en la base de datos
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBanco;

    @Column(name = "nombre_banco", nullable = false, length = 100, unique = true)
    private String nombreBanco;

    @Column(name = "codigo_banco", length = 10, unique = true)
    private String codigoBanco;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}