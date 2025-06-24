package com.textilima.textilima.repository;

import com.textilima.textilima.entities.Empleado;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    // Método para encontrar empleados por estado (activo/inactivo)
    List<Empleado> findByEstado(Boolean estado);

    // Método para buscar empleados por nombre o apellido (ignorando
    // mayúsculas/minúsculas)
    List<Empleado> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);

    // Método para encontrar un empleado por su número de documento (DNI/CE)
    Optional<Empleado> findByNumeroDocumento(String numeroDocumento);


}
