package com.textilima.textilima.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textilima.textilima.model.ControlVacacion;
import com.textilima.textilima.model.Empleado;

public interface ControlVacacionRepository extends JpaRepository<ControlVacacion, Integer> {
    /**
     * Busca un registro de control de vacaciones para un empleado y un período específico.
     * @param empleado El empleado.
     * @param periodo El período de vacaciones (ej. "2023-2024").
     * @return Un Optional que contiene el ControlVacacion encontrado, o vacío si no existe.
     */
    Optional<ControlVacacion> findByEmpleadoAndPeriodo(Empleado empleado, String periodo);

    /**
     * Busca todos los registros de control de vacaciones para un empleado.
     * @param empleado El empleado.
     * @return Una lista de registros de ControlVacacion para el empleado.
     */
    List<ControlVacacion> findByEmpleado(Empleado empleado);

    /**
     * Busca registros de control de vacaciones donde los días pendientes sean mayores a cero
     * para un empleado específico.
     * @param empleado El empleado.
     * @return Una lista de ControlVacacion con días pendientes.
     */
    List<ControlVacacion> findByEmpleadoAndDiasPendientesGreaterThan(Empleado empleado, Integer diasPendientes);
}
