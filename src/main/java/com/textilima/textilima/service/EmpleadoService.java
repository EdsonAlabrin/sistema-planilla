// src/main/java/com/textilima/textilima/service/EmpleadoService.java
package com.textilima.textilima.service;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Puesto; // Importar Puesto si se usa en algún Javadoc o firma

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmpleadoService {
    /**
     * Obtiene una lista de todos los empleados.
     * @return Una lista de objetos Empleado.
     */
    List<Empleado> getAllEmpleados();

    /**
     * Obtiene un empleado por su ID.
     * @param idEmpleado El ID del empleado.
     * @return Un Optional que contiene el Empleado si se encuentra, o vacío si no existe.
     */
    Optional<Empleado> getEmpleadoById(Integer idEmpleado);

    /**
     * Guarda un empleado y gestiona automáticamente su historial de puesto.
     * Si es un nuevo empleado, crea un HistorialPuesto inicial.
     * Si es un empleado existente y su puesto ha cambiado, actualiza el HistorialPuesto anterior
     * y crea uno nuevo.
     * @param empleado El objeto Empleado a guardar/actualizar.
     * @return El Empleado guardado/actualizado.
     */
    Empleado saveEmpleadoAndManagePuestoHistory(Empleado empleado);

    /**
     * Elimina un empleado por su ID.
     * @param idEmpleado El ID del empleado a eliminar.
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

    /**
     * Busca empleados por una cadena de consulta que puede coincidir con DNI, nombres o apellidos.
     * @param query La cadena de texto a buscar.
     * @return Una lista de empleados que coinciden con la búsqueda.
     */
    List<Empleado> searchEmpleados(String query); // Método de búsqueda unificado
}
