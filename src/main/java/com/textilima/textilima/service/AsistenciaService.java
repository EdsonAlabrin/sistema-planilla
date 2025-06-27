package com.textilima.textilima.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Asistencia;
import com.textilima.textilima.model.Empleado;

public interface AsistenciaService {
    /**
     * Obtiene una lista de todas las asistencias.
     * 
     * @return Una lista de objetos Asistencia.
     */
    List<Asistencia> getAllAsistencias();

    /**
     * Obtiene una asistencia por su ID.
     * 
     * @param id El ID de la asistencia.
     * @return Un Optional que contiene la Asistencia si se encuentra, o vacío si no
     *         existe.
     */
    Optional<Asistencia> getAsistenciaById(Integer idAsistencia);

    /**
     * Guarda una nueva asistencia o actualiza una existente.
     * 
     * @param asistencia El objeto Asistencia a guardar/actualizar.
     * @return La Asistencia guardada/actualizada.
     */
    Asistencia saveAsistencia(Asistencia asistencia);

    /**
     * Elimina una asistencia por su ID.
     * 
     * @param id El ID de la asistencia a eliminar.
     */
    void deleteAsistencia(Integer idAsistencia);

    /**
     * Busca la asistencia de un empleado para una fecha específica.
     * 
     * @param empleado El empleado.
     * @param fecha    La fecha de la asistencia.
     * @return Un Optional que contiene la Asistencia encontrada, o vacío si no
     *         existe.
     */
    Optional<Asistencia> getAsistenciaByEmpleadoAndFecha(Empleado empleado, LocalDate fecha);

    /**
     * Busca todas las asistencias de un empleado en un rango de fechas.
     * 
     * @param empleado    El empleado.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin    La fecha de fin del rango (inclusive).
     * @return Una lista de asistencias para el empleado en el rango especificado.
     */
    List<Asistencia> getAsistenciasByEmpleadoAndFechaBetween(Empleado empleado, LocalDate fechaInicio,
            LocalDate fechaFin);

    /**
     * Busca asistencias con estado de tardanza o ausencia para un empleado en un
     * rango de fechas.
     * Esto es útil para calcular descuentos en la planilla.
     * 
     * @param empleado    El empleado.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin    La fecha de fin del rango (inclusive).
     * @param estados     Los estados de asistencia a buscar (ej.
     *                    Asistencia.EstadoAsistencia.TARDANZA,
     *                    Asistencia.EstadoAsistencia.AUSENTE).
     * @return Una lista de asistencias que coinciden con los estados y el rango de
     *         fechas.
     */
    List<Asistencia> getAsistenciasByEmpleadoAndFechaBetweenAndEstados(Empleado empleado, LocalDate fechaInicio,
            LocalDate fechaFin, List<Asistencia.EstadoAsistencia> estados);
}
