package com.textilima.textilima.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Empleado;

public interface EmpleadoService {
    /**
     * Obtiene una lista de todos los empleados.
     * @return Una lista de objetos Empleado.
     */
    List<Empleado> getAllEmpleados();

    /**
     * Obtiene un empleado por su ID.
     * @param id El ID del empleado.
     * @return Un Optional que contiene el Empleado si se encuentra, o vacío si no existe.
     */
    Optional<Empleado> getEmpleadoById(Integer idEmpleado);

    /**
     * Guarda un nuevo empleado o actualiza uno existente.
     * @param empleado El objeto Empleado a guardar/actualizar.
     * @return El Empleado guardado/actualizado.
     */
    Empleado saveEmpleado(Empleado empleado);

    /**
     * Elimina un empleado por su ID.
     * @param id El ID del empleado a eliminar.
     */
    void deleteEmpleado(Integer idEmpleado);

    /**
     * Busca un empleado por su número de documento (DNI, Carné de Extranjería, etc.).
     * @param numeroDocumento El número de documento del empleado.
     * @return Un Optional que contiene el Empleado encontrado, o vacío si no existe.
     */
    Optional<Empleado> getEmpleadoByNumeroDocumento(String numeroDocumento);

    /**
     * Busca todos los empleados que están actualmente activos (estado = true).
     * @return Una lista de empleados activos.
     */
    List<Empleado> getActiveEmpleados();

    /**
     * Busca todos los empleados que tienen hijos calificados para asignación familiar.
     * @return Una lista de empleados con hijos calificados.
     */
    List<Empleado> getEmpleadosWithCalifiedChildren();

    /**
     * Busca empleados cuya fecha de ingreso se encuentra dentro de un rango especificado.
     * @param startDate La fecha de inicio del rango (inclusive).
     * @param endDate La fecha de fin del rango (inclusive).
     * @return Una lista de empleados que ingresaron en el rango de fechas.
     */
    List<Empleado> getEmpleadosByFechaIngresoBetween(LocalDate startDate, LocalDate endDate);
}
