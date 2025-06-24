package com.textilima.textilima.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bonos")
@EqualsAndHashCode(exclude = { "detallePlanilla", "conceptoPago" })
@ToString(exclude = { "detallePlanilla", "conceptoPago" })
public class Bono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBono;

    // mappedBy en DetallePlanilla apunta a este campo
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle", nullable = false)
    private DetallePlanilla detallePlanilla;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) // Un bono siempre debe tener un concepto asociado
    @JoinColumn(name = "id_concepto", nullable = false)
    private ConceptoPago conceptoPago;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

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