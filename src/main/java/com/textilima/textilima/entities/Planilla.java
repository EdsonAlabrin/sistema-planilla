package com.textilima.textilima.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "planillas")
public class Planilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlanilla;

    @Column(nullable = false)
    private Integer mes;
    
    @Column(nullable = false)
    private Integer anio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_planilla", nullable = false, length = 20)
    private TipoPlanilla tipoPlanilla;

    @Column(name = "fecha_generada", nullable = false)
    private LocalDate fechaGenerada;

    @OneToMany(mappedBy = "planilla", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetallePlanilla> detalles;

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

    public enum TipoPlanilla {
        MENSUAL, GRATIFICACION, CTS, VACACION, LBS
    }
}
