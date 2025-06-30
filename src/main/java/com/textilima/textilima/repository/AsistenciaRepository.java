package com.textilima.textilima.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Asistencia;
import com.textilima.textilima.model.Empleado;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {
    /**
     * Busca la asistencia de un empleado para una fecha específica.
     * @param empleado El empleado.
     * @param fecha La fecha de la asistencia.
     * @return Un Optional que contiene la Asistencia encontrada, o vacío si no existe.
     */
    Optional<Asistencia> findByEmpleadoAndFecha(Empleado empleado, LocalDate fecha);

    /**
     * Busca todas las asistencias de un empleado en un rango de fechas.
     * @param empleado El empleado.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin La fecha de fin del rango (inclusive).
     * @return Una lista de asistencias para el empleado en el rango especificado.
     */
    List<Asistencia> findByEmpleadoAndFechaBetween(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca asistencias con estado de tardanza o ausencia para un empleado en un rango de fechas.
     * Esto es útil para calcular descuentos en la planilla.
     * @param empleado El empleado.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin La fecha de fin del rango (inclusive).
     * @param estados Los estados de asistencia a buscar (ej. Asistencia.EstadoAsistencia.TARDANZA, Asistencia.EstadoAsistencia.AUSENTE).
     * @return Una lista de asistencias que coinciden con los estados y el rango de fechas.
     */
    List<Asistencia> findByEmpleadoAndFechaBetweenAndEstadoIn(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin, List<Asistencia.EstadoAsistencia> estados);
}
