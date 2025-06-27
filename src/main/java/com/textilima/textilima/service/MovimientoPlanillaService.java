package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.MovimientoPlanilla;

public interface MovimientoPlanillaService {
    /**
     * Obtiene una lista de todos los movimientos de planilla.
     * @return Una lista de objetos MovimientoPlanilla.
     */
    List<MovimientoPlanilla> getAllMovimientosPlanilla();

    /**
     * Obtiene un movimiento de planilla por su ID.
     * @param id El ID del movimiento de planilla.
     * @return Un Optional que contiene el MovimientoPlanilla si se encuentra, o vacío si no existe.
     */
    Optional<MovimientoPlanilla> getMovimientoPlanillaById(Integer idMovimiento);

    /**
     * Guarda un nuevo movimiento de planilla o actualiza uno existente.
     * @param movimientoPlanilla El objeto MovimientoPlanilla a guardar/actualizar.
     * @return El MovimientoPlanilla guardado/actualizado.
     */
    MovimientoPlanilla saveMovimientoPlanilla(MovimientoPlanilla movimientoPlanilla);

    /**
     * Elimina un movimiento de planilla por su ID.
     * @param id El ID del movimiento de planilla a eliminar.
     */
    void deleteMovimientoPlanilla(Integer idMovimiento);

    /**
     * Busca todos los movimientos de planilla asociados a un detalle de planilla específico.
     * @param detallePlanilla El detalle de planilla al que pertenecen los movimientos.
     * @return Una lista de MovimientoPlanilla para el detalle de planilla especificado.
     */
    List<MovimientoPlanilla> getMovimientosByDetallePlanilla(DetallePlanilla detallePlanilla);

    /**
     * Busca un movimiento de planilla por el detalle de planilla y el concepto de pago.
     * @param detallePlanilla El detalle de planilla.
     * @param concepto El concepto de pago.
     * @return Un Optional que contiene el MovimientoPlanilla encontrado, o vacío.
     */
    Optional<MovimientoPlanilla> getMovimientoByDetallePlanillaAndConcepto(DetallePlanilla detallePlanilla, ConceptoPago concepto);

    /**
     * Busca movimientos de planilla por un concepto de pago específico.
     * @param concepto El concepto de pago.
     * @return Una lista de MovimientoPlanilla que utilizan el concepto especificado.
     */
    List<MovimientoPlanilla> getMovimientosByConcepto(ConceptoPago concepto);
}
