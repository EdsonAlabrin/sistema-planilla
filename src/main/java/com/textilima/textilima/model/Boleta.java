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

   
    // Este constructor permite crear una Boleta con los campos mínimos necesarios.
    // Lombok generará el constructor sin argumentos gracias a @NoArgsConstructor.
    // Si necesitas el constructor con TODOS los campos para otras operaciones, puedes dejar @AllArgsConstructor
    // o crear un constructor manual que incluya todos los campos.
    public Boleta(DetallePlanilla detallePlanilla, LocalDate fechaEmision, String rutaPdf) {
        this.detallePlanilla = detallePlanilla;
        this.fechaEmision = fechaEmision;
        this.rutaPdf = rutaPdf;
        // Los campos 'firmada' y 'enviada' se inicializan con sus valores por defecto (false)
        // 'createdAt' y 'updatedAt' serán manejados por @CreationTimestamp/@UpdateTimestamp
    }
}
