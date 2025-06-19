package com.textilima.textilima.repository;

import com.textilima.textilima.entities.Empleado;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
     // Método para encontrar un empleado por su número de documento (DNI)
    Optional<Empleado> findByNumeroDocumento(String numeroDocumento);

    // Método para encontrar empleados por su estado (activo/inactivo)
    List<Empleado> findByEstado(Boolean estado);

    // Método para buscar empleados por nombre o apellido (ejemplo de consulta personalizada)
    // Spring Data JPA puede generar esto automáticamente si los nombres de los métodos siguen las convenciones
    List<Empleado> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);

    // Puedes añadir más métodos según tus necesidades (ej. paginación, ordenación, otras búsquedas)
    
}

