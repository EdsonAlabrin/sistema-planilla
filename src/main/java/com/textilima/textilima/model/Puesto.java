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

    @Column(name = "jornada_laboral_horas", precision = 4, scale = 2)
    private BigDecimal jornadaLaboralHoras; // Puede ser decimal (ej. 8.50 horas)

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Sugerencia de mejora de la base de datos:
     * Si los salarios base cambian con el tiempo y necesitas un histórico,
     * podrías considerar una tabla separada 'historial_salarios_puesto'
     * o añadir una fecha de vigencia a esta tabla para registrar los cambios.
     * Por ahora, se mantiene el diseño original de la tabla 'puestos'.
     */
}
