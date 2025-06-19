package com.textilima.textilima.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empleados")
public class Empleado {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpleado;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "tipo_documento", length = 10)
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 1)
    private String sexo;

    @Column(name = "estado_civil", length = 50)
    private String estadoCivil;

    @Column(length = 100)
    private String nacionalidad;

    @Column(length = 100)
    private String correo;

    @Column(name = "direccion_completa", length = 255)
    private String direccionCompleta;

    @Column(length = 100)
    private String distrito;

    @Column(length = 100)
    private String provincia;

    @Column(length = 100)
    private String departamento;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    @Column(nullable = false)
    private Boolean estado;

    @Column(name = "regimen_laboral", length = 50)
    private String regimenLaboral;

    @Column(name = "numero_hijos")
    private Integer hijos; // Campo para el número de hijos

    @Enumerated(EnumType.STRING)
    @Column(name = "sistema_pensiones", length = 10)
    private SistemaPensiones sistemaPensiones; // ONP, AFP

    @Column(name = "codigo_pension", length = 50)
    private String codigoPension;

    @Column(name = "nombre_afp", length = 100)
    private String nombreAfp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banco")
    private Banco banco; // Relación con Banco

    @Column(name = "numero_cuenta_banco", length = 50)
    private String numeroCuentaBanco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puesto", nullable = false)
    private Puesto puesto; // Relación con Puesto

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

    public enum SistemaPensiones {
        ONP, AFP
    }
}