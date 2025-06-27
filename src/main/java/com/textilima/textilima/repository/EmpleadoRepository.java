package com.textilima.textilima.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Empleado;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    /**
     * Busca un empleado por su número de documento (DNI, Carné de Extranjería, etc.).
     * @param numeroDocumento El número de documento del empleado.
     * @return Un Optional que contiene el Empleado encontrado, o vacío si no existe.
     */
    Optional<Empleado> findByNumeroDocumento(String numeroDocumento);

    /**
     * Busca todos los empleados que tienen un estado específico.
     * @param estado El estado del empleado (true para activo, false para inactivo/cesado).
     * @return Una lista de empleados que coinciden con el estado especificado.
     */
    List<Empleado> findByEstado(Boolean estado);

    /**
     * Busca todos los empleados que tienen hijos calificados para asignación familiar.
     * @param tieneHijosCalificados Booleano que indica si tiene hijos calificados (true) o no (false).
     * @return Una lista de empleados con hijos calificados.
     */
    List<Empleado> findByTieneHijosCalificados(Boolean tieneHijosCalificados);

    /**
     * Busca empleados cuya fecha de ingreso se encuentra dentro de un rango especificado.
     * @param startDate La fecha de inicio del rango (inclusive).
     * @param endDate La fecha de fin del rango (inclusive).
     * @return Una lista de empleados que ingresaron en el rango de fechas.
     */
    List<Empleado> findByFechaIngresoBetween(LocalDate startDate, LocalDate endDate);

}
