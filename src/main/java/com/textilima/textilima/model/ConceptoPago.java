// src/main/java/com/textilima/textilima/model/ConceptoPago.java
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
 * Entidad que define los diferentes conceptos de pago (ingresos, descuentos, aportes).
 * Mapea a la tabla `conceptos_pago`.
 * Es crucial para la configuración flexible y dinámica de la planilla.
 */
@Entity
@Table(name = "conceptos_pago")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class ConceptoPago { // Se usa "ConceptoPago" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_concepto")
    private Integer idConcepto;

    @Column(name = "codigo_sunat", length = 10)
    private String codigoSunat; // Código SUNAT para reportes

    @Column(name = "nombre_concepto", nullable = false, unique = true, length = 100)
    private String nombreConcepto;

    @Enumerated(EnumType.STRING) // Almacenar el nombre del enum como String en la base de datos
    @Column(name = "tipo", nullable = false, length = 20) // 'INGRESO','DESCUENTO','APORTE_EMPLEADOR'
    private TipoConcepto tipo;

    @Enumerated(EnumType.STRING) // Almacenar el nombre del enum como String en la base de datos
    @Column(name = "metodo_calculo", nullable = false, length = 20) // 'PORCENTAJE','MONTO_FIJO','FORMULA_ESPECIAL'
    private MetodoCalculo metodoCalculo;

    @Column(name = "es_remunerativo")
    private Boolean esRemunerativo; // Indica si el concepto es remunerativo (afecto a beneficios sociales)

    @Column(name = "valor_referencial", precision = 10, scale = 4)
    private BigDecimal valorReferencial; // Porcentaje o monto fijo de referencia

    @Column(name = "afecto_onp")
    private Boolean afectoOnp; // Afecto a Sistema Nacional de Pensiones

    @Column(name = "afecto_afp")
    private Boolean afectoAfp; // Afecto a Administradoras de Fondos de Pensiones

    @Column(name = "afecto_essalud")
    private Boolean afectoEssalud; // Afecto a EsSalud (Seguro Social de Salud)

    @Column(name = "descripcion", columnDefinition = "TEXT") // TEXT para descripciones largas
    private String descripcion;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Enum para el tipo de concepto
    public enum TipoConcepto {
        INGRESO, DESCUENTO, APORTE_EMPLEADOR
    }

    // Enum para el método de cálculo
    public enum MetodoCalculo {
        PORCENTAJE, MONTO_FIJO, FORMULA_ESPECIAL
    }

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se utilizan BigDecimal para valor_referencial para asegurar la precisión.
     * - Se utilizan Enums para 'tipo' y 'metodo_calculo' para asegurar los valores permitidos y la legibilidad.
     * - El campo 'descripcion' utiliza columnDefinition = "TEXT" para mapearse a un tipo TEXT en la BD,
     * permitiendo descripciones más largas.
     *
     * Sugerencias adicionales:
     * - Asegúrate de que los valores de afecto_onp, afecto_afp, y afecto_essalud estén
     * correctamente configurados para cada concepto según la normativa laboral peruana.
     * - El 'FORMULA_ESPECIAL' en metodo_calculo implica que la lógica de cálculo
     * para estos conceptos debe ser manejada por la aplicación.
     */
}
