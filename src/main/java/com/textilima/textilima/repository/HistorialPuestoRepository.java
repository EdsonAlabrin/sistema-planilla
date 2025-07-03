// src/main/java/com/textilima/textilima/repository/HistorialPuestoRepository.java
package com.textilima.textilima.repository;

import com.textilima.textilima.model.HistorialPuesto;

import jakarta.transaction.Transactional;

import com.textilima.textilima.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialPuestoRepository extends JpaRepository<HistorialPuesto, Integer> {
    /**
     * Busca el historial de puestos de un empleado específico.
     * Los resultados se ordenan por fecha de inicio descendente.
     * @param empleado El empleado para el cual buscar el historial.
     * @return Una lista de registros de historial de puestos para el empleado.
     */
    List<HistorialPuesto> findByEmpleadoOrderByFechaInicioDesc(Empleado empleado);

    /**
     * Busca el puesto actual de un empleado (aquel que no tiene fecha_fin).
     * @param empleado El empleado para el cual buscar el puesto actual.
     * @return Un Optional que contiene el registro de HistorialPuesto del puesto actual, o vacío si no se encuentra.
     */
    Optional<HistorialPuesto> findByEmpleadoAndFechaFinIsNull(Empleado empleado);

    /**
     * Busca el historial de puestos para un empleado que estuvieron activos en una fecha específica.
     * La fecha proporcionada debe estar entre la fecha de inicio y la fecha de fin (o fecha de fin nula).
     * @param empleado El empleado.
     * @param fecha La fecha para la cual se busca el puesto activo.
     * @return Un Optional que contiene el HistorialPuesto activo en esa fecha, o vacío.
     */
    @Query("SELECT hp FROM HistorialPuesto hp WHERE hp.empleado = :empleado AND :fecha BETWEEN hp.fechaInicio AND COALESCE(hp.fechaFin, :fecha)")
    Optional<HistorialPuesto> findByEmpleadoAndDate(@Param("empleado") Empleado empleado, @Param("fecha") LocalDate fecha);

    //referenciar correctamente el ID del Empleado
    @Modifying
    @Transactional
    void deleteByEmpleado_IdEmpleado(Integer empleadoId); 

    // Si necesitas buscar por ID de empleado para otras operaciones antes de eliminar
    List<HistorialPuesto> findByEmpleado_IdEmpleado(Integer empleadoId);
}
