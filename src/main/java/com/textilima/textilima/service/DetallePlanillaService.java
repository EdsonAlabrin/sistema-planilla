package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Planilla;

public interface DetallePlanillaService {
  /**
     * Obtiene una lista de todos los detalles de planilla.
     * @return Una lista de objetos DetallePlanilla.
     */
    List<DetallePlanilla> getAllDetallesPlanilla();

    /**
     * Obtiene un detalle de planilla por su ID.
     * @param idDetalle El ID del detalle de planilla.
     * @return Un Optional que contiene el DetallePlanilla si se encuentra, o vacío si no existe.
     */
    Optional<DetallePlanilla> getDetallePlanillaById(Integer idDetalle);

    /**
     * Guarda un nuevo detalle de planilla o actualiza uno existente.
     * @param detallePlanilla El objeto DetallePlanilla a guardar/actualizar.
     * @return El DetallePlanilla guardado/actualizado.
     */
    DetallePlanilla saveDetallePlanilla(DetallePlanilla detallePlanilla);

    /**
     * Elimina un detalle de planilla por su ID.
     * @param idDetalle El ID del detalle de planilla a eliminar.
     */
    void deleteDetallePlanilla(Integer idDetalle);

    /**
     * Busca un detalle de planilla específico por la planilla y el empleado.
     * @param planilla La planilla a la que pertenece el detalle.
     * @param empleado El empleado al que corresponde el detalle.
     * @return Un Optional que contiene el DetallePlanilla encontrado, o vacío si no existe.
     */
    Optional<DetallePlanilla> getDetallePlanillaByPlanillaAndEmpleado(Planilla planilla, Empleado empleado);

    /**
     * Busca todos los detalles de una planilla específica.
     * @param planilla La planilla de la cual se desean obtener los detalles.
     * @return Una lista de DetallePlanilla para la planilla especificada.
     */
    List<DetallePlanilla> getDetallesByPlanilla(Planilla planilla);

    /**
     * Busca todos los detalles de planilla de un empleado en particular.
     * @param empleado El empleado del cual se desean obtener los detalles de planilla.
     * @return Una lista de DetallePlanilla para el empleado especificado.
     */
    List<DetallePlanilla> getDetallesPlanillaByEmpleado(Empleado empleado);

    // Métodos para cálculos específicos en el DetallePlanilla (ej. recalcular totales)
    // BigDecimal calcularRemuneracionComputableAfecta(DetallePlanilla detalle);
    // BigDecimal calcularSueldoBruto(DetallePlanilla detalle);
    // BigDecimal calcularSueldoNeto(DetallePlanilla detalle);
}