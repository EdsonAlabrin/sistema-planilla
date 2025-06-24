package com.textilima.textilima.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textilima.textilima.entities.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {
    /**
         * Busca una asistencia por ID de empleado y fecha.
         * Esto es útil para verificar si un empleado ya tiene una asistencia registrada en un día específico.
         * @param idEmpleado El ID del empleado.
         * @param fecha La fecha de la asistencia.
         * @return Un Optional que contiene la Asistencia si se encuentra, o vacío si no.
         */
        Optional<Asistencia> findByEmpleado_IdEmpleadoAndFecha(Integer idEmpleado, LocalDate fecha);

        /**
         * Busca todas las asistencias para un empleado específico, ordenadas por fecha descendente.
         * @param idEmpleado El ID del empleado.
         * @return Una lista de asistencias del empleado.
         */
        List<Asistencia> findByEmpleado_IdEmpleadoOrderByFechaDesc(Integer idEmpleado);

        /**
         * Busca asistencias dentro de un rango de fechas para un empleado específico.
         * @param idEmpleado El ID del empleado.
         * @param fechaInicio La fecha de inicio del rango (inclusive).
         * @param fechaFin La fecha de fin del rango (inclusive).
         * @return Una lista de asistencias que caen dentro del rango.
         */
        List<Asistencia> findByEmpleado_IdEmpleadoAndFechaBetween(Integer idEmpleado, LocalDate fechaInicio, LocalDate fechaFin);

        /**
         * Busca todas las asistencias para una fecha específica.
         * @param fecha La fecha a buscar.
         * @return Una lista de asistencias para esa fecha.
         */
        List<Asistencia> findByFecha(LocalDate fecha);

}
