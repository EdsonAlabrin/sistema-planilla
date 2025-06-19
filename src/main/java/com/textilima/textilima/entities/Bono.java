package com.textilima.textilima.entities;



import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bonos")
public class Bono {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBono;

    @ManyToOne // Un bono pertenece a un detalle de planilla
    @JoinColumn(name = "id_detalle", nullable = false)
    private DetallePlanilla detallePlanilla;

    @ManyToOne // Un bono está asociado a un tipo de concepto de pago
    @JoinColumn(name = "id_concepto", nullable = false)
    private ConceptoPago conceptoPago;

    // Cambiado a BigDecimal
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
}
