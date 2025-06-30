// src/main/java/com/textilima/textilima/model/Asistencia.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Duration;
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

    @ManyToOne(fetch = FetchType.LAZY) // Muchas asistencias para un empleado
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "minutos_tardanza")
    private Integer minutosTardanza = 0; // Por defecto 0, si no hay tardanza

    @Column(name = "horas_extras_25", precision = 5, scale = 2)
    private BigDecimal horasExtras25 = BigDecimal.ZERO; // Horas extras al 25%

    @Column(name = "horas_extras_35", precision = 5, scale = 2)
    private BigDecimal horasExtras35 = BigDecimal.ZERO; // Horas extras al 35%

    @Enumerated(EnumType.STRING) // Almacenar el nombre del enum como String
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoAsistencia estado = EstadoAsistencia.PRESENTE; // Estado de la asistencia

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Enum para el estado de asistencia
    public enum EstadoAsistencia {
        PRESENTE("Presente"),
        TARDANZA("Tardanza"),
        AUSENTE("Ausente");

        private final String displayName;

        EstadoAsistencia(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Método transitorio para calcular las horas trabajadas en formato HH:mm:ss.
     * No se mapea a una columna en la base de datos.
     * @return String con las horas trabajadas formateadas.
     */
    @Transient // Indica que este campo no se mapea a una columna de BD
    public String getHorasTrabajadasFormateadas() {
        if (this.horaEntrada == null || this.horaSalida == null) {
            return "--:--:--";
        }
        Duration duration = Duration.between(this.horaEntrada, this.horaSalida);
        if (duration.isNegative()) {
            return "00:00:00"; // Si la salida es antes de la entrada, asumimos 0
        }
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
}
