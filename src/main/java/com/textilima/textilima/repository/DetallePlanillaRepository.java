package com.textilima.textilima.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Planilla;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetallePlanillaRepository extends JpaRepository<DetallePlanilla, Integer> {
  /**
     * Busca un detalle de planilla específico por la planilla y el empleado.
     * @param planilla La planilla a la que pertenece el detalle.
     * @param empleado El empleado al que corresponde el detalle.
     * @return Un Optional que contiene el DetallePlanilla encontrado, o vacío si no existe.
     */
    Optional<DetallePlanilla> findByPlanillaAndEmpleado(Planilla planilla, Empleado empleado);

    /**
     * Busca todos los detalles de una planilla específica.
     * @param planilla La planilla de la cual se desean obtener los detalles.
     * @return Una lista de DetallePlanilla para la planilla especificada.
     */
    List<DetallePlanilla> findByPlanilla(Planilla planilla);

    /**
     * Busca todos los detalles de planilla de un empleado en particular.
     * @param empleado El empleado del cual se desean obtener los detalles de planilla.
     * @return Una lista de DetallePlanilla para el empleado especificado.
     */
    List<DetallePlanilla> findByEmpleado(Empleado empleado);
}

