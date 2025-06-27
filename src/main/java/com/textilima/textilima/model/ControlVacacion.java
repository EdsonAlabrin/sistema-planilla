// src/main/java/com/textilima/textilima/model/ControlVacacion.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Entidad que controla los días de vacaciones ganados, gozados y pendientes por empleado y periodo.
 * Mapea a la tabla `control_vacaciones`.
 * Es fundamental para la gestión y seguimiento de las vacaciones de los empleados.
 */
@Entity
@Table(name = "control_vacaciones")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class ControlVacacion { // Se usa "ControlVacacion" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_control_vac")
    private Integer idControlVac;

    @ManyToOne // Muchos registros de control de vacaciones pueden pertenecer a un empleado
    @JoinColumn(name = "id_empleado", nullable = false) // Columna en `control_vacaciones` que referencia a `empleados`
    private Empleado empleado;

    @Column(name = "periodo", nullable = false, length = 9) // Ej. "2023-2024"
    private String periodo;

    @Column(name = "dias_ganados")
    private Integer diasGanados; // Por defecto 30 días en Perú

    @Column(name = "dias_gozados")
    private Integer diasGozados;

    @Column(name = "dias_pendientes")
    private Integer diasPendientes;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se mapea la relación ManyToOne con Empleado.
     * - La clave única uq_empleado_periodo_vacaciones en la base de datos
     * asegura un único registro de vacaciones por empleado y periodo.
     * Es crucial para el cálculo y la trazabilidad de las vacaciones.
     */
}
