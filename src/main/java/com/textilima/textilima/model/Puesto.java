// src/main/java/com/textilima/textilima/model/Puesto.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

/**
 * Entidad que representa los puestos de trabajo en la empresa.
 * Mapea a la tabla `puestos`.
 * Incluye el salario base y la jornada laboral.
 */
@Entity
@Table(name = "puestos")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Puesto { // Se usa "Puesto" en singular para la entidad

  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puesto")
    private Integer idPuesto;

    @Column(name = "nombre_puesto", nullable = false, unique = true, length = 100)
    private String nombrePuesto;

    @Column(name = "salario_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase; // Usar BigDecimal para precisión monetaria

    @Column(name = "jornada_laboral_horas")
    private Integer jornadaLaboralHoras; // Horas de la jornada laboral (ej. 8)

    @Column(name = "hora_inicio_jornada") // Nueva columna para la hora de inicio de la jornada
    private LocalTime horaInicioJornada;

    @Column(name = "hora_fin_jornada") // Nueva columna para la hora de fin de la jornada
    private LocalTime horaFinJornada;

    @Column(name = "descripcion", columnDefinition = "TEXT") // TEXT para descripciones largas
    private String descripcion;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se usa BigDecimal para salario_base para asegurar la precisión.
     * - Se mapea a la tabla 'puestos'.
     * - Se añaden horaInicioJornada y horaFinJornada para el cálculo preciso de asistencia.
     * - El campo 'descripcion' utiliza columnDefinition = "TEXT" para mapearse a un tipo TEXT en la BD,
     * permitiendo descripciones más largas.
     */
}
