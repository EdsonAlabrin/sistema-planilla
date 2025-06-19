package com.textilima.textilima.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor; // Mantenemos AllArgsConstructor para otros usos (si los hay)
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // Genera getters, setters, equals, hashCode, toString
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // !IMPORTANTE: Aunque está aquí, si el problema persiste, el constructor de abajo lo sobreescribe para ese fin.
@Entity
@Table(name = "conceptos_pago")
public class ConceptoPago {
    

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConcepto;

    @Column(length = 10)
    private String codigoSunat;

    @Column(nullable = false, unique = true, length = 100)
    private String nombreConcepto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConcepto tipo;

    @Column(name = "es_remunerativo")
    private Boolean esRemunerativo;

    @Column(name = "es_obligatorio")
    private Boolean esObligatorio;

    @Column(name = "aplica_monto_fijo")
    private boolean aplicaMontoFijo;

    @Column(precision = 10, scale = 4)
    private BigDecimal valor;

    @Column(name = "afecto_ir")
    private Boolean afectoIR;
    
    @Column(name = "afecto_onp")
    private Boolean afectoONP;

    @Column(name = "afecto_afp")
    private Boolean afectoAFP;

    @Column(name = "afecto_essalud")
    private Boolean afectoEssalud;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TipoConcepto {
        INGRESO, DESCUENTO, APORTE_EMPLEADOR, APORTE_EMPLEADO
    }
}
