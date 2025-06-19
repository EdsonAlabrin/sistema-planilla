package com.textilima.textilima.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ¡¡¡ASEGÚRATE QUE ESTE IMPORT ESTÉ PRESENTE!!!
import com.textilima.textilima.entities.ConceptoPago; 


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_planilla")
public class DetallePlanilla {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDetalle;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_planilla", nullable = false)
    private Planilla planilla;

    // ¡¡¡CAMPO 'empleado' CON NOMBRE CORRECTO Y RELACIÓN MAPPED POR JPA!!!
    @ManyToOne(optional = false) // Un detalle de planilla siempre debe estar asociado a un empleado
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado; // Este nombre de campo generará el método getEmpleado()

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_concepto_pago", nullable = true)
    private ConceptoPago conceptoPago;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal sueldoBruto;

    @Column(name = "remuneracion_computable", precision = 10, scale = 2, nullable = false)
    private BigDecimal remuneracionComputable;

    @Column(name = "asignacion_familiar", precision = 10, scale = 2, nullable = false)
    private BigDecimal asignacionFamiliar;

    @Column(name = "total_bonos", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalBonos;

    @Column(name = "total_descuentos", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalDescuentos;

    @Column(name = "aporte_pension_empleado", precision = 10, scale = 2, nullable = false)
    private BigDecimal aportePensionEmpleado;

    @Column(name = "impuesto_renta_5ta", precision = 10, scale = 2, nullable = false)
    private BigDecimal impuestoRenta5ta;

    @Column(name = "sueldo_neto", precision = 10, scale = 2, nullable = false)
    private BigDecimal sueldoNeto;

    @Column(name = "aporte_essalud_empleador", precision = 10, scale = 2, nullable = false)
    private BigDecimal aporteEssaludEmpleador;

    // Relaciones OneToMany para Bonos y Descuentos
    // Asegúrate de que DetallePlanilla_idDetalle sea la columna en Bono/Descuento que apunta a este DetallePlanilla
    @OneToMany(mappedBy = "detallePlanilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bono> bonos = new HashSet<>();

    @OneToMany(mappedBy = "detallePlanilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Descuento> descuentos = new HashSet<>();

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