// src/main/java/com/textilima/textilima/model/Planilla.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate; // Importar LocalDate para fechas sin hora
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa la cabecera de una planilla de pago.
 * Mapea a la tabla `planillas`.
 * Define el periodo (mes, año), tipo de planilla y fecha de generación.
 */
@Entity
@Table(name = "planillas")
@Data// Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Planilla { // Se usa "Planilla" en singular para la entidad
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planilla")
    private Integer idPlanilla;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Enumerated(EnumType.STRING) // Almacenar el nombre del enum como String en la base de datos
    @Column(name = "tipo_planilla", nullable = false, length = 20) // 'CTS','GRATIFICACION','LBS','MENSUAL','VACACION'
    private TipoPlanilla tipoPlanilla;

    @Column(name = "fecha_generada", nullable = false)
    private LocalDate fechaGenerada;

    @ManyToOne(fetch = FetchType.LAZY) // Muchas planillas pueden referenciar un mismo parámetro legal (RMV, etc.)
    @JoinColumn(name = "id_param_rmv") // Columna en `planillas` que referencia a `parametros_legales`
    private ParametroLegal paramRmv; // Referencia al parámetro legal de RMV vigente en la fecha de la planilla

    // Relación OneToMany con DetallePlanilla
    // MappedBy indica el nombre del campo en la entidad DetallePlanilla que posee
    // la relación.
    // CascadeType.ALL significa que las operaciones (persist, merge, remove,
    // refresh, detach)
    // realizadas en la entidad Planilla se propagarán a sus entidades
    // DetallePlanilla asociadas.
    // orphanRemoval = true asegura que si un DetallePlanilla es desvinculado de la
    // lista, sea eliminado.
    @OneToMany(mappedBy = "planilla", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetallePlanilla> detallesPlanilla = new ArrayList<>();

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Enum para el tipo de planilla
    public enum TipoPlanilla {
        MENSUAL("Mensual"); // Puedes añadir más si necesitas

        private final String displayName; // Campo para el nombre legible

        TipoPlanilla(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}
