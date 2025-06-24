package com.textilima.textilima.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
// Dentro de la clase Asistencia (o en un DTO)
import java.time.Duration; // Asegúrate de importar Duration

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // Genera Getters, Setters, toString, equals y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor 
@Table(name = "asistencias", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_empleado", "fecha"})})
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Integer idAsistencia; // ID de asistencia (INT en DB)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado; // Relación con la entidad Empleado

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha; // Fecha de la asistencia (DATE en DB)

    @Enumerated(EnumType.STRING) // Mapea el ENUM de MySQL a un ENUM de Java (PRESENTE, AUSENTE, TARDANZA)
    @Column(name = "estado", nullable = false)
    private EstadoAsistencia estado; // Estado de la asistencia

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada; // Hora de entrada (TIME en DB)

    @Column(name = "hora_salida")
    private LocalTime horaSalida; // Hora de salida (TIME en DB)

    @Column(name = "created_at", updatable = false) // Campo para la fecha de creación (TIMESTAMP en DB)
    private LocalDateTime createdAt;

    @Column(name = "updated_at") // Campo para la fecha de última actualización (TIMESTAMP en DB)
    private LocalDateTime updatedAt;

    // Un constructor adicional, si se desea, para una inicialización específica.
    // Lombok se encargará de los constructores sin argumentos y con todos los argumentos.
    public Asistencia(Empleado empleado, LocalDate fecha, EstadoAsistencia estado) {
        this.empleado = empleado;
        this.fecha = fecha;
        this.estado = estado;
    }

    // --- Métodos de ciclo de vida de JPA para gestionar timestamps ---
    // Estos métodos se ejecutan automáticamente antes de persistir (guardar por primera vez)
    // o antes de actualizar la entidad en la base de datos.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Se ejecuta antes de cada actualización de la entidad.
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getHorasTrabajadasFormateadas() {
    if (this.horaEntrada != null && this.horaSalida != null) {
        Duration duration = Duration.between(this.horaEntrada, this.horaSalida);
        // Si la hora de salida es antes de la hora de entrada (cruce de medianoche), ajusta
        if (duration.isNegative()) {
            duration = duration.plusDays(1); // Suma un día para calcular correctamente
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    return "--:--:--";
}
}



