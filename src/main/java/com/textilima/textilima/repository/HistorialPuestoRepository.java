package com.textilima.textilima.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.HistorialPuesto;

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
     * Busca el puesto actual de un empleado (aquel que no tiene fecha_fin o tiene la fecha_fin más reciente).
     * @param empleado El empleado para el cual buscar el puesto actual.
     * @return Un Optional que contiene el registro de HistorialPuesto del puesto actual, o vacío si no se encuentra.
     */
    Optional<HistorialPuesto> findTopByEmpleadoAndFechaFinIsNullOrderByFechaInicioDesc(Empleado empleado);

    /**
     * Busca el historial de puestos para un empleado que estuvieron activos en una fecha específica.
     * @param empleado El empleado.
     * @param fecha La fecha para la cual se busca el puesto activo.
     * @return Un Optional que contiene el HistorialPuesto activo en esa fecha, o vacío.
     */
    Optional<HistorialPuesto> findByEmpleadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqualOrFechaFinIsNull(
            Empleado empleado, LocalDate fecha, LocalDate fecha2); // fecha2 se usa para la condición OrFechaFinIsNull
}
