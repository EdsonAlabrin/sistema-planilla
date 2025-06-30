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

@Entity
@Table(name = "movimientos_planilla")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoPlanilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento; // ID es Integer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle", nullable = false)
    private DetallePlanilla detallePlanilla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_concepto", nullable = false)
    private ConceptoPago concepto; // Usa ConceptoPago

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // CONSTRUCTOR ADICIONADO para uso en el controlador al generar
    public MovimientoPlanilla(Integer idMovimiento, DetallePlanilla detallePlanilla, ConceptoPago concepto, BigDecimal monto) {
        this.idMovimiento = idMovimiento;
        this.detallePlanilla = detallePlanilla;
        this.concepto = concepto;
        this.monto = monto;
    }
}
