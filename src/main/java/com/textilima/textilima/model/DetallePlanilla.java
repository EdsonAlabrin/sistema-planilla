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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /// Relación OneToMany con MovimientoPlanilla
    // mappedBy indica el campo en la entidad MovimientoPlanilla que es el propietario de la relación (el ManyToOne)
    // CascadeType.ALL significa que si eliminas un DetallePlanilla, se eliminarán sus movimientos asociados
    // orphanRemoval = true asegura que si un MovimientoPlanilla se desvincula de este detalle, también se elimine de la BD
    @OneToMany(mappedBy = "detallePlanilla", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MovimientoPlanilla> movimientosPlanilla = new ArrayList<>(); // Inicializar para evitar NullPointerException

    // Campo transitorio para indicar si se generó la boleta (no se persiste en BD)
    @Transient
    private boolean boletaGenerada;

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

    // --- Métodos de conveniencia para manejar la lista de movimientos ---
    // Estos son los métodos que faltaban y causaban el error.
    public void addMovimientoPlanilla(MovimientoPlanilla movimiento) {
        if (this.movimientosPlanilla == null) {
            this.movimientosPlanilla = new ArrayList<>();
        }
        this.movimientosPlanilla.add(movimiento);
        movimiento.setDetallePlanilla(this); // Asegura la relación bidireccional
    }

    public void removeMovimientoPlanilla(MovimientoPlanilla movimiento) {
        if (this.movimientosPlanilla != null) {
            this.movimientosPlanilla.remove(movimiento);
            movimiento.setDetallePlanilla(null); // Rompe la relación bidireccional
        }
    }

    // Getter para boleta que devuelve Optional para un manejo más seguro
    public Optional<Boleta> getBoleta() {
        return Optional.ofNullable(boleta);
    }

    // Setter para boleta
    public void setBoleta(Boleta boleta) {
        this.boleta = boleta;
        if (boleta != null && boleta.getDetallePlanilla() != this) {
            boleta.setDetallePlanilla(this); // Asegura la relación bidireccional
        }
    }

}
