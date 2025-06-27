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

/**
 * Entidad que representa la cabecera de una planilla de pago.
 * Mapea a la tabla `planillas`.
 * Define el periodo (mes, año), tipo de planilla y fecha de generación.
 */
@Entity
@Table(name = "planillas")
@Data // Genera getters, setters, toString, equals, hashCode
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

    @ManyToOne // Muchas planillas pueden referenciar un mismo parámetro legal (RMV, etc.)
    @JoinColumn(name = "id_param_rmv") // Columna en `planillas` que referencia a `parametros_legales`
    private ParametroLegal paramRmv; // Referencia al parámetro legal de RMV vigente en la fecha de la planilla

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Enum para el tipo de planilla
    public enum TipoPlanilla {
        CTS, GRATIFICACION, LBS, MENSUAL, VACACION
    }

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se usa LocalDate para la fecha_generada.
     * - Se mapea la relación ManyToOne con ParametroLegal (para RMV).
     * - Se utiliza un Enum para 'tipo_planilla' para asegurar los valores permitidos.
     * - La clave única uq_planilla_mes_anio_tipo en la base de datos
     * asegura una única planilla por mes, año y tipo.
     *
     * Sugerencias adicionales para el futuro:
     * - Se podría añadir un campo 'estado_planilla' (ej. 'PENDIENTE', 'CALCULADA', 'APROBADA', 'PAGADA', 'CERRADA')
     * para controlar el flujo de trabajo de la planilla.
     * - Considerar agregar un 'fecha_pago' si se maneja por tipo de planilla.
     */
}
