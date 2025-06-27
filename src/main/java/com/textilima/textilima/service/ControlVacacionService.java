package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.ControlVacacion;
import com.textilima.textilima.model.Empleado;

public interface ControlVacacionService {
    /**
     * Obtiene una lista de todos los registros de control de vacaciones.
     * @return Una lista de objetos ControlVacacion.
     */
    List<ControlVacacion> getAllControlVacaciones();

    /**
     * Obtiene un registro de control de vacaciones por su ID.
     * @param id El ID del registro de control de vacaciones.
     * @return Un Optional que contiene el ControlVacacion si se encuentra, o vacío si no existe.
     */
    Optional<ControlVacacion> getControlVacacionById(Integer idControlVac);

    /**
     * Guarda un nuevo registro de control de vacaciones o actualiza uno existente.
     * @param controlVacacion El objeto ControlVacacion a guardar/actualizar.
     * @return El ControlVacacion guardado/actualizado.
     */
    ControlVacacion saveControlVacacion(ControlVacacion controlVacacion);

    /**
     * Elimina un registro de control de vacaciones por su ID.
     * @param id El ID del registro de control de vacaciones a eliminar.
     */
    void deleteControlVacacion(Integer idControlVac);

    /**
     * Busca un registro de control de vacaciones para un empleado y un período específico.
     * @param empleado El empleado.
     * @param periodo El período de vacaciones (ej. "2023-2024").
     * @return Un Optional que contiene el ControlVacacion encontrado, o vacío si no existe.
     */
    Optional<ControlVacacion> getControlVacacionByEmpleadoAndPeriodo(Empleado empleado, String periodo);

    /**
     * Busca todos los registros de control de vacaciones para un empleado.
     * @param empleado El empleado.
     * @return Una lista de registros de ControlVacacion para el empleado.
     */
    List<ControlVacacion> getControlVacacionesByEmpleado(Empleado empleado);

    /**
     * Busca registros de control de vacaciones donde los días pendientes sean mayores a cero
     * para un empleado específico.
     * @param empleado El empleado.
     * @return Una lista de ControlVacacion con días pendientes.
     */
    List<ControlVacacion> getControlVacacionesWithPendingDaysByEmpleado(Empleado empleado);
}
