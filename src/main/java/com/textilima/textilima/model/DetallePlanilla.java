// src/main/java/com/textilima/textilima/model/DetallePlanilla.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entidad que representa el detalle de una planilla por empleado.
 * Mapea a la tabla `detalle_planilla`.
 * Contiene los totales calculados de ingresos, descuentos y aportes para un empleado en una planilla específica.
 */
@Entity
@Table(name = "detalle_planilla")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class DetallePlanilla { // Se usa "DetallePlanilla" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne // Muchos detalles pueden pertenecer a una misma planilla
    @JoinColumn(name = "id_planilla", nullable = false) // Columna en `detalle_planilla` que referencia a `planillas`
    private Planilla planilla;

    @ManyToOne // Muchos detalles pueden pertenecer a un mismo empleado (en diferentes planillas)
    @JoinColumn(name = "id_empleado", nullable = false) // Columna en `detalle_planilla` que referencia a `empleados`
    private Empleado empleado;

    @Column(name = "sueldo_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoBase;

    @Column(name = "asignacion_familiar", precision = 10, scale = 2)
    private BigDecimal asignacionFamiliar;

    @Column(name = "remuneracion_computable_afecta", nullable = false, precision = 10, scale = 2)
    private BigDecimal remuneracionComputableAfecta;

    @Column(name = "total_ingresos_adicionales", precision = 10, scale = 2)
    private BigDecimal totalIngresosAdicionales;

    @Column(name = "total_descuentos", precision = 10, scale = 2)
    private BigDecimal totalDescuentos;

    @Column(name = "total_aportes_empleador", precision = 10, scale = 2)
    private BigDecimal totalAportesEmpleador;

    @Column(name = "sueldo_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoBruto;

    @Column(name = "sueldo_neto", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoNeto;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se mapean las relaciones ManyToOne con Planilla y Empleado.
     * - Se utilizan BigDecimal para todos los campos monetarios para asegurar la precisión.
     * - Los campos como remuneracion_computable_afecta, total_ingresos_adicionales,
     * total_descuentos, total_aportes_empleador, sueldo_bruto, y sueldo_neto
     * son campos calculados. Es crucial que la lógica de la aplicación sea
     * la única responsable de calcular y almacenar estos valores correctamente
     * para evitar inconsistencias.
     * - La clave única uq_detalle_planilla_empleado en la base de datos
     * asegura un único detalle de planilla por planilla y empleado.
     */
}
