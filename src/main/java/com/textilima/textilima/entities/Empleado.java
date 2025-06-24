package com.textilima.textilima.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode; // Necesario para @EqualsAndHashCode.Exclude
import lombok.NoArgsConstructor;
import lombok.ToString;        // Necesario para @ToString.Exclude

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet; // Si usas Set para colecciones
import java.util.Set; 


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"historialPuestos", "usuarios", "puesto", "banco"})
@ToString(exclude = {"historialPuestos", "usuarios", "puesto", "banco"})
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

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Sexo sexo; // Enum para sexo

    @Column(name = "estado_civil", length = 20)
    private String estadoCivil;

    @Column(length = 50)
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

    @Column(nullable = false)
    private Boolean estado; // True = activo, False = inactivo

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    // ¡¡¡CAMPO 'numeroHijos' CON NOMBRE CORRECTO!!!
    @Column(name = "numero_hijos")
    private Integer numeroHijos; // Este nombre de campo generará getNumeroHijos()

    @Enumerated(EnumType.STRING)
    @Column(name = "sistema_pensiones", length = 10)
    private SistemaPensiones sistemaPensiones; // Enum para sistema de pensiones

    @Column(name = "codigo_pension", length = 50)
    private String codigoPension;

    @Column(name = "nombre_afp", length = 50)
    private String nombreAfp;

    @Column(name = "numero_cuenta_banco", length = 50)
    private String numeroCuentaBanco;

    @Column(name = "regimen_laboral", nullable = false, length = 50)
    private String regimenLaboral;

    // Relaciones ManyToOne
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puesto", nullable = false)
    private Puesto puesto;

    @ManyToOne(fetch = FetchType.LAZY) // Banco puede ser opcional
    @JoinColumn(name = "id_banco")
    private Banco banco;

    // Relaciones OneToMany (si existieran, para evitar StackOverflowError)
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<HistorialPuesto> historialPuestos = new HashSet<>();

    // Si también tienes una relación OneToOne o OneToMany con Usuario (si un empleado es también un usuario)
    @OneToOne(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Usuario usuario; // Si la relación es OneToOne

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

    // Enumeraciones anidadas
    public enum Sexo {
        M, F
    }

    public enum SistemaPensiones {
        ONP, AFP
    }
}