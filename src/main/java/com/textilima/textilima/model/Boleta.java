// src/main/java/com/textilima/textilima/model/Boleta.java
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
 * Entidad que representa la boleta de pago generada.
 * Mapea a la tabla `boletas`.
 * Contiene información sobre la emisión, ruta del PDF, y estados de firma y envío.
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
    @JoinColumn(name = "id_detalle", nullable = false) // Columna en `boletas` que referencia a `detalle_planilla`
    private DetallePlanilla detallePlanilla;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

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

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se mapea la relación OneToOne con DetallePlanilla. La base de datos
     * tiene ON DELETE CASCADE en esta relación, lo que significa que si un
     * DetallePlanilla se elimina, la Boleta asociada también se eliminará.
     * - Se usa LocalDate para la fecha_emision.
     * - Se utilizan Boolean para los campos 'firmada' y 'enviada'.
     *
     * Sugerencias adicionales:
     * - Asegúrate de que 'ruta_pdf' almacene rutas relativas o URLs a un
     * sistema de almacenamiento de archivos seguro, y no los PDFs directamente.
     * - Podrías añadir un campo 'fecha_firma' o 'fecha_envio' para mayor detalle en la auditoría.
     */
}
