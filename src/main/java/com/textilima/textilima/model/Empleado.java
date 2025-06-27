// src/main/java/com/textilima/textilima/model/Empleado.java
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
 * Entidad que representa la información de los empleados.
 * Mapea a la tabla `empleados`.
 * Contiene datos personales, laborales y de sistemas de pensiones.
 */
@Entity
@Table(name = "empleados")
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Empleado { // Se usa "Empleado" en singular para la entidad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "tipo_documento", length = 10)
    private String tipoDocumento; // Ej. DNI, Carné de Extranjería

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "sexo", length = 1)
    private String sexo; // Ej. 'M' para Masculino, 'F' para Femenino

    @Column(name = "estado_civil", length = 20)
    private String estadoCivil;

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
    private Boolean estado; // true para activo, false para inactivo/cesado

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    @ManyToOne // Muchos empleados pueden tener un puesto
    @JoinColumn(name = "id_puesto", nullable = false) // Columna en `empleados` que referencia a `puestos`
    private Puesto puesto;

    @Column(name = "regimen_laboral", nullable = false, length = 50)
    private String regimenLaboral; // Ej. "Regimen General", "MYPE"

    @Column(name = "tiene_hijos_calificados")
    private Boolean tieneHijosCalificados; // 1 si tiene hijos menores de 18 o cursando estudios superiores, 0 si no

    @Enumerated(EnumType.STRING) // Almacenar el nombre del enum como String en la base de datos
    @Column(name = "sistema_pensiones", length = 3) // "AFP" o "ONP"
    private SistemaPensiones sistemaPensiones;

    @Column(name = "codigo_pension", length = 50)
    private String codigoPension; // CUSPP para AFP

    @Column(name = "nombre_afp", length = 50)
    private String nombreAfp; // Solo si sistema_pensiones es AFP

    @Column(name = "numero_cuenta_banco", length = 50)
    private String numeroCuentaBanco;

    @ManyToOne // Muchos empleados pueden usar un mismo banco
    @JoinColumn(name = "id_banco") // Columna en `empleados` que referencia a `bancos` (puede ser nulo)
    private Banco banco;

    // Campos de auditoría automática
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Enum para sistema de pensiones
    public enum SistemaPensiones {
        AFP, ONP
    }

    /*
     * Observaciones de la revisión de la base de datos aplicadas:
     * - Se usa LocalDate para campos de fecha sin hora.
     * - Se mapean las relaciones ManyToOne con Puesto y Banco.
     * - Se utiliza un Enum para 'sistema_pensiones' para asegurar los valores permitidos.
     *
     * Sugerencias adicionales:
     * - Para 'estado', se mantiene Boolean. Si se necesita mayor granularidad (ACTIVO, INACTIVO, CESADO),
     * se podría cambiar a un Enum o String con más valores.
     * - Para los campos condicionales de AFP (codigoPension, nombreAfp), se mantienen como parte de la entidad.
     * Si la lógica de AFP se vuelve muy compleja, se podría considerar una tabla separada.
     */
}
