package com.textilima.textilima.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.HistorialPuesto;

public interface HistorialPuestoService {
  
    /**
     * Obtiene una lista de todos los registros de historial de puestos.
     * @return Una lista de objetos HistorialPuesto.
     */
    List<HistorialPuesto> getAllHistorialPuestos();

    /**
     * Obtiene un registro de historial de puesto por su ID.
     * @param id El ID del registro de historial de puesto.
     * @return Un Optional que contiene el HistorialPuesto si se encuentra, o vacío si no existe.
     */
    Optional<HistorialPuesto> getHistorialPuestoById(Integer idHistoial);

    /**
     * Guarda un nuevo registro de historial de puesto o actualiza uno existente.
     * @param historialPuesto El objeto HistorialPuesto a guardar/actualizar.
     * @return El HistorialPuesto guardado/actualizado.
     */
    HistorialPuesto saveHistorialPuesto(HistorialPuesto historialPuesto);

    /**
     * Elimina un registro de historial de puesto por su ID.
     * @param id El ID del registro de historial de puesto a eliminar.
     */
    void deleteHistorialPuesto(Integer idHistorial);

    /**
     * Busca el historial de puestos de un empleado específico, ordenados por fecha de inicio descendente.
     * @param empleado El empleado para el cual buscar el historial.
     * @return Una lista de registros de historial de puestos para el empleado.
     */
    List<HistorialPuesto> getHistorialPuestosByEmpleado(Empleado empleado);

    /**
     * Busca el puesto actual de un empleado (aquel que no tiene fecha_fin o tiene la fecha_fin más reciente).
     * @param empleado El empleado para el cual buscar el puesto actual.
     * @return Un Optional que contiene el registro de HistorialPuesto del puesto actual, o vacío si no se encuentra.
     */
    Optional<HistorialPuesto> getCurrentPuestoByEmpleado(Empleado empleado);

    /**
     * Busca el historial de puestos para un empleado que estuvieron activos en una fecha específica.
     * @param empleado El empleado.
     * @param fecha La fecha para la cual se busca el puesto activo.
     * @return Un Optional que contiene el HistorialPuesto activo en esa fecha, o vacío.
     */
    Optional<HistorialPuesto> getPuestoByEmpleadoAndDate(Empleado empleado, LocalDate fecha);
}
