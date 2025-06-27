// src/main/java/com/textilima/textilima/model/MovimientoPlanilla.java
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
 * Entidad que registra los movimientos de pago específicos (ingresos, descuentos, aportes)
 * para un empleado dentro de un detalle de planilla.
 * Mapea a la tabla `movimientos_planilla`.
 * Es esencial para el desglose detallado de la boleta de pago de cada empleado.
 */
@Entity
@Table(name = "movimientos_planilla")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class MovimientoPlanilla { // Se usa "MovimientoPlanilla" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @ManyToOne // Muchos movimientos pueden pertenecer a un mismo detalle de planilla
    @JoinColumn(name = "id_detalle", nullable = false) // Columna en `movimientos_planilla` que referencia a `detalle_planilla`
    private DetallePlanilla detallePlanilla;

    @ManyToOne // Muchos movimientos pueden referirse al mismo concepto de pago
    @JoinColumn(name = "id_concepto", nullable = false) // Columna en `movimientos_planilla` que referencia a `conceptos_pago`
    private ConceptoPago concepto;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se mapean las relaciones ManyToOne con DetallePlanilla y ConceptoPago.
     * - Se utiliza BigDecimal para el campo 'monto' para asegurar la precisión monetaria.
     * - Esta tabla es esencial para el desglose detallado de los ingresos, descuentos
     * y aportes que componen el sueldo neto de cada empleado en una planilla.
     * - Asegúrate de que esta tabla se pueble dinámicamente con todos los conceptos
     * aplicables a cada empleado en cada planilla calculada.
     */
}
