package com.textilima.textilima.service;


    

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.entities.Asistencia;
import com.textilima.textilima.entities.Asistencia;
import com.textilima.textilima.entities.Empleado;

public interface AsistenciaService {
    /**
         * Obtiene todas las asistencias registradas.
         * @return Una lista de todas las asistencias.
         */
        List<Asistencia> obtenerTodasAsistencias();

        /**
         * Obtiene una asistencia por su ID.
         * @param id El ID de la asistencia.
         * @return Un Optional que contiene la Asistencia si se encuentra, o vacío si no.
         */
        Optional<Asistencia> obtenerAsistenciaPorId(Integer idAsistencia);

        /**
         * Guarda una nueva asistencia o actualiza una existente.
         * @param asistencia La asistencia a guardar.
         * @return La asistencia guardada.
         */
        Asistencia saveAsistencia(Asistencia asistencia);

        /**
         * Elimina una asistencia por su ID.
         * @param id El ID de la asistencia a eliminar.
         */
        void deleteAsistencia(Integer idAsistencia);

        /**
         * Registra la entrada de un empleado para la fecha actual.
         * Si ya existe una asistencia para el día, la actualiza, de lo contrario crea una nueva.
         * @param empleado El empleado que registra la entrada.
         * @param horaEntrada La hora de entrada.
         * @return La asistencia actualizada o creada.
         */
        Asistencia registrarEntrada(Empleado empleado, LocalTime horaEntrada);

        /**
         * Registra la salida de un empleado para la fecha actual.
         * Solo funciona si ya existe un registro de entrada para el día.
         * @param empleado El empleado que registra la salida.
         * @param horaSalida La hora de salida.
         * @return La asistencia actualizada o null si no se encontró un registro de entrada para ese día.
         */
        Asistencia registrarSalida(Empleado empleado, LocalTime horaSalida);

        /**
         * Busca una asistencia específica por empleado y fecha.
         * @param idEmpleado El ID del empleado.
         * @param fecha La fecha de la asistencia.
         * @return Un Optional que contiene la Asistencia si se encuentra, o vacío si no.
         */
        Optional<Asistencia> findAsistenciaByEmpleadoAndFecha(Integer idEmpleado, LocalDate fecha);

        /**
         * Obtiene un listado de asistencias para un empleado en un rango de fechas.
         * @param idEmpleado El ID del empleado.
         * @param fechaInicio La fecha de inicio del rango.
         * @param fechaFin La fecha de fin del rango.
         * @return Una lista de asistencias del empleado en el rango especificado.
         */
        List<Asistencia> obtenerAsistenciasByEmpleadoAndDateRange(Integer idEmpleado, LocalDate fechaInicio, LocalDate fechaFin);

        /**
         * Obtiene un listado de todas las asistencias para una fecha específica.
         * @param fecha La fecha a buscar.
         * @return Una lista de asistencias para esa fecha.
         */
        List<Asistencia> obtenerAsistenciasByFecha(LocalDate fecha);
}
