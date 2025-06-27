// src/main/java/com/textilima/textilima/model/HistorialPuesto.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate; // Importar LocalDate para fechas sin hora

/**
 * Entidad que registra el historial de puestos de un empleado en la empresa.
 * Mapea a la tabla `historial_puestos`.
 * Es crucial para el seguimiento de la trayectoria laboral y cálculos de antigüedad.
 */
@Entity
@Table(name = "historial_puestos")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class HistorialPuesto { // Se usa "HistorialPuesto" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne // Muchos registros de historial pueden pertenecer a un empleado
    @JoinColumn(name = "id_empleado", nullable = false) // Columna en `historial_puestos` que referencia a `empleados`
    private Empleado empleado;

    @ManyToOne // Muchos registros de historial pueden referirse a un puesto
    @JoinColumn(name = "id_puesto", nullable = false) // Columna en `historial_puestos` que referencia a `puestos`
    private Puesto puesto;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin; // Opcional, si el empleado sigue en ese puesto

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se usa LocalDate para campos de fecha sin hora.
     * - Se mapean las relaciones ManyToOne con Empleado y Puesto.
     * - Es importante que la lógica de la aplicación maneje correctamente
     * las fechas de inicio y fin para evitar solapamientos o huecos
     * en el historial de un empleado.
     */
}
