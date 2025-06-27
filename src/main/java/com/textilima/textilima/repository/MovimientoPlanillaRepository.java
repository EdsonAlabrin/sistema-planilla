package com.textilima.textilima.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.MovimientoPlanilla;

public interface MovimientoPlanillaRepository extends JpaRepository<MovimientoPlanilla, Integer> {
    /**
     * Busca todos los movimientos de planilla asociados a un detalle de planilla específico.
     * @param detallePlanilla El detalle de planilla al que pertenecen los movimientos.
     * @return Una lista de MovimientoPlanilla para el detalle de planilla especificado.
     */
    List<MovimientoPlanilla> findByDetallePlanilla(DetallePlanilla detallePlanilla);

    /**
     * Busca un movimiento de planilla por el detalle de planilla y el concepto de pago.
     * Esto podría ser útil para verificar si un concepto ya ha sido aplicado en un detalle específico.
     * @param detallePlanilla El detalle de planilla.
     * @param concepto El concepto de pago.
     * @return Un Optional que contiene el MovimientoPlanilla encontrado, o vacío.
     */
    Optional<MovimientoPlanilla> findByDetallePlanillaAndConcepto(DetallePlanilla detallePlanilla, ConceptoPago concepto);

    /**
     * Busca movimientos de planilla por un concepto de pago específico.
     * @param concepto El concepto de pago.
     * @return Una lista de MovimientoPlanilla que utilizan el concepto especificado.
     */
    List<MovimientoPlanilla> findByConcepto(ConceptoPago concepto);
}
