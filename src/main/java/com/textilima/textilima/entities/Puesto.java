package com.textilima.textilima.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "puestos")
public class Puesto {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPuesto;

    @Column(name = "nombre_puesto", nullable = false, length = 100)
    private String nombrePuesto;

    @Column(name = "salario_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "jornada_laboral_horas", precision = 4, scale = 2)
    private BigDecimal jornadaLaboralHoras;

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

    
}
