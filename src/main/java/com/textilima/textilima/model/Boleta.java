// src/main/java/com/textilima/textilima/model/Boleta.java
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
 * Entidad que representa la boleta de pago generada.
 * Mapea a la tabla `boletas`.
 * Contiene información sobre la emisión, ruta del PDF, y estados de firma y
 * envío.
 */
@Entity
@Table(name = "boletas")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Boleta { // Se usa "Boleta" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleta")
    private Integer idBoleta;

    @OneToOne // Una boleta corresponde a un único detalle de planilla
    @JoinColumn(name = "id_detalle", nullable = false, unique = true) // Columna en `boletas` que referencia a `detalle_planilla`
    private DetallePlanilla detallePlanilla;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    // --- ¡CAMPOS FALTANTES AÑADIDOS AQUÍ! ---
    @Column(name = "periodo_mes", nullable = false)
    private Integer periodoMes;

    @Column(name = "periodo_anio", nullable = false)
    private Integer periodoAnio;

    @Column(name = "sueldo_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoBruto;

    @Column(name = "total_descuentos", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDescuentos;

    @Column(name = "sueldo_neto", nullable = false, precision = 10, scale = 2)
    private BigDecimal sueldoNeto;
    // --- FIN CAMPOS FALTANTES ---

    @Column(name = "ruta_pdf", length = 255)
    private String rutaPdf; // Ruta o URL al archivo PDF de la boleta

    @Column(name = "firmada")
    private Boolean firmada; // true si ha sido firmada, false en caso contrario

    @Column(name = "enviada")
    private Boolean enviada; // true si ha sido enviada al empleado, false en caso contrario

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Constructor adicional para facilitar la creación en el servicio/controlador
    // Este constructor debe coincidir con la llamada en PlanillaController
    public Boleta(DetallePlanilla detallePlanilla, LocalDate fechaEmision, Integer periodoMes, Integer periodoAnio, BigDecimal sueldoBruto, BigDecimal totalDescuentos, BigDecimal sueldoNeto) {
        this.detallePlanilla = detallePlanilla;
        this.fechaEmision = fechaEmision;
        this.periodoMes = periodoMes; // Asignación del parámetro al campo
        this.periodoAnio = periodoAnio; // Asignación del parámetro al campo
        this.sueldoBruto = sueldoBruto;
        this.totalDescuentos = totalDescuentos;
        this.sueldoNeto = sueldoNeto;
    }
}
