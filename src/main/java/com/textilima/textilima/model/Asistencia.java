// src/main/java/com/textilima/textilima/model/Asistencia.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate; // Importar LocalDate para fechas sin hora
import java.time.LocalTime; // Importar LocalTime para horas sin fecha

/**
 * Entidad que registra la información de asistencia de los empleados.
 * Mapea a la tabla `asistencias`.
 * Incluye estado de asistencia, horas de entrada/salida y cálculos de tardanzas/horas extras.
 */
@Entity
@Table(name = "asistencias")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Asistencia { // Se usa "Asistencia" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Integer idAsistencia;

    @ManyToOne // Muchas asistencias pueden pertenecer a un empleado
    @JoinColumn(name = "id_empleado", nullable = false) // Columna en `asistencias` que referencia a `empleados`
    private Empleado empleado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING) // Almacenar el nombre del enum como String en la base de datos
    @Column(name = "estado", nullable = false, length = 20) // 'PRESENTE','AUSENTE','TARDANZA','FALTA_JUSTIFICADA'
    private EstadoAsistencia estado;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "minutos_tardanza")
    private Integer minutosTardanza;

    @Column(name = "horas_extras_25", precision = 4, scale = 2)
    private BigDecimal horasExtras25; // Usar BigDecimal para precisión decimal

    @Column(name = "horas_extras_35", precision = 4, scale = 2)
    private BigDecimal horasExtras35; // Usar BigDecimal para precisión decimal

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Enum para el estado de asistencia
    public enum EstadoAsistencia {
        PRESENTE, AUSENTE, TARDANZA, FALTA_JUSTIFICADA
    }

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se usa LocalDate para la fecha y LocalTime para las horas.
     * - Se mapea la relación ManyToOne con Empleado.
     * - Se utiliza un Enum para 'estado' de la asistencia para asegurar los valores permitidos.
     * - Se usan BigDecimal para horas_extras_25 y horas_extras_35 para precisión.
     * - La clave única uq_empleado_fecha_asistencia en la base de datos
     * asegura una única asistencia por empleado por día.
     */
}
