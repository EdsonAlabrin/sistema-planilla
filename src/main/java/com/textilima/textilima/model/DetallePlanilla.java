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
import java.util.List;

/**
 * Entidad que representa el detalle de una planilla por empleado.
 * Mapea a la tabla `detalle_planilla`.
 */
@Entity
@Table(name = "detalle_planilla")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePlanilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    // Relación ManyToOne con Planilla: Cada detalle pertenece a una planilla.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planilla", nullable = false)
    private Planilla planilla;

    // Relación ManyToOne con Empleado: Cada detalle es para un empleado específico.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
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

    // Relación OneToMany con MovimientoPlanilla: Un detalle puede tener varios movimientos (ingresos/descuentos).
    // mappedBy indica que la relación es bidireccional y la columna FK está en MovimientoPlanilla.
    // CascadeType.ALL significa que operaciones como guardar o eliminar se propagarán.
    @OneToMany(mappedBy = "detallePlanilla", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MovimientoPlanilla> movimientosPlanilla;

    // --- NUEVA RELACIÓN OneToOne con Boleta ---
    // mappedBy indica que la columna de clave foránea reside en la tabla 'boletas'.
    // FetchType.LAZY para evitar cargar la boleta a menos que sea necesario.
    // optional = true porque no todos los DetallePlanilla tendrán una Boleta asociada.
    @OneToOne(mappedBy = "detallePlanilla", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Boleta boleta;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // PROPIEDAD TRANSITORIA para saber si ya se generó una boleta (NO SE MAPEA A LA BD)
    @Transient
    private Boolean boletaGenerada = false;
}
