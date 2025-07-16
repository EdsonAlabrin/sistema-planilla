// src/main/java/com/textilima/textilima/model/Empleado.java
package com.textilima.textilima.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.textilima.textilima.model.enums.EstadoCivil;
import com.textilima.textilima.model.enums.RegimenLaboral;
import com.textilima.textilima.model.enums.Sexo;
import com.textilima.textilima.model.enums.TipoDocumento;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado; // ID es Integer

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Enumerated(EnumType.STRING) // ¡Asegúrate de que esta anotación esté aquí! Almacena el nombre del enum (e.g., "DNI")
    @Column(name = "tipo_documento", length = 10)
    private TipoDocumento tipoDocumento; // CAMBIADO a TipoDocumento

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento; // CAMBIADO de 'dni' a 'numeroDocumento' para coincidir

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING) // ¡Asegúrate de esta anotación!
    @Column(name = "sexo", length = 1)
    private Sexo sexo;

    @Enumerated(EnumType.STRING) // ¡Asegúrate de esta anotación!
    @Column(name = "estado_civil", length = 20)
    private EstadoCivil estadoCivil;

    @Column(name = "nacionalidad", length = 50)
    private String nacionalidad;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "direccion_completa", length = 255)
    private String direccionCompleta;

    @Column(name = "distrito", length = 100)
    private String distrito;

    @Column(name = "provincia", length = 100)
    private String provincia;

    @Column(name = "departamento", length = 100)
    private String departamento;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    // Asumo que Puesto y Banco existen y tienen IDs Integer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puesto", nullable = false)
    private Puesto puesto;

    @Enumerated(EnumType.STRING) // ¡Asegúrate de esta anotación!
    @Column(name = "regimen_laboral", nullable = false, length = 50)
    private RegimenLaboral regimenLaboral;

    @Column(name = "tiene_hijos_calificados", nullable = false) 
    private Boolean tieneHijosCalificados; 

    @Enumerated(EnumType.STRING) // ¡Asegúrate de esta anotación!
    @Column(name = "sistema_pensiones", length = 10)
    private SistemaPensiones sistemaPensiones;

    @Column(name = "codigo_pension", length = 50)
    private String codigoPension;

    @Column(name = "nombre_afp", length = 50)
    private String nombreAfp;

    @Column(name = "numero_cuenta_banco", length = 50)
    private String numeroCuentaBanco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banco")
    private Banco banco;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum SistemaPensiones {
        AFP, ONP
    }
}

// ** NOTA IMPORTANTE: Necesitas las entidades Puesto.java y Banco.java
// Asegúrate de que sus IDs también sean Integer si aún no lo son.
// Puesto.java:
// @Entity @Table(name = "puestos") @Data @NoArgsConstructor @AllArgsConstructor
// public class Puesto { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer idPuesto; ... }
// Banco.java:
// @Entity @Table(name = "bancos") @Data @NoArgsConstructor @AllArgsConstructor
// public class Banco { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer idBanco; ... }
