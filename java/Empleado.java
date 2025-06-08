package com.miempresa.planilla.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;
    private String dni;
    private String cargo;
    private String area;
    private Double sueldo;

    private LocalDate fechaIngreso;
    private Boolean activo;

    // Getters y Setters
}
