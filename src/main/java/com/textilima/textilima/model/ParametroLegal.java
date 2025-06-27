// src/main/java/com/textilima/textilima/model/ParametroLegal.java
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

/**
 * Entidad que representa los parámetros legales y valores de referencia
 * como la Remuneración Mínima Vital (RMV) o la Unidad Impositiva Tributaria (UIT).
 * Mapea a la tabla `parametros_legales`.
 * Esta tabla es crucial para manejar valores cambiantes a lo largo del tiempo.
 */
@Entity
@Table(name = "parametros_legales")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class ParametroLegal { // Se usa "ParametroLegal" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Integer idParametro;

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo; // Ej. "RMV", "UIT"

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor; // Usar BigDecimal para precisión monetaria

    @Column(name = "fecha_vigencia_inicio", nullable = false)
    private LocalDate fechaVigenciaInicio; // Fecha desde que el parámetro es válido

    @Column(name = "fecha_vigencia_fin")
    private LocalDate fechaVigenciaFin; // Fecha hasta que el parámetro es válido (opcional)

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /*
     * Observaciones de la revisión de la base de datos:
     * La clave única uq_codigo_fecha_inicio en la base de datos
     * (UNIQUE KEY `uq_codigo_fecha_inicio` (`codigo`,`fecha_vigencia_inicio`))
     * es fundamental y asegura la unicidad por parámetro y vigencia.
     * La lógica de la aplicación debe asegurarse de consultar el valor
     * con la fecha_vigencia_inicio más reciente que sea menor o igual
     * a la fecha de la planilla que se está calculando.
     */
}
