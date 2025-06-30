package com.textilima.textilima.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.MovimientoPlanilla;

@Repository
public interface MovimientoPlanillaRepository extends JpaRepository<MovimientoPlanilla, Integer> {
    /**
     * Busca todos los movimientos de planilla asociados a un detalle de planilla específico.
     * @param detallePlanilla The detail of the payroll to which the movements belong.
     * @return A list of MovimientoPlanilla for the specified payroll detail.
     */
    List<MovimientoPlanilla> findByDetallePlanilla(DetallePlanilla detallePlanilla);

    /**
     * Busca un movimiento de planilla por el detalle de planilla y el concepto de pago.
     * Esto podría ser útil para verificar si un concepto ya ha sido aplicado en un detalle específico.
     * @param detallePlanilla The payroll detail.
     * @param concepto The payment concept.
     * @return An Optional containing the found MovimientoPlanilla, or empty.
     */
    Optional<MovimientoPlanilla> findByDetallePlanillaAndConcepto(DetallePlanilla detallePlanilla, ConceptoPago concepto);

    /**
     * Busca movimientos de planilla por un concepto de pago específico.
     * @param concepto The payment concept.
     * @return A list of MovimientoPlanilla that use the specified concept.
     */
    List<MovimientoPlanilla> findByConcepto(ConceptoPago concepto);

    /**
     * Finds all payroll movements associated with a specific payroll detail ID.
     * This method has been moved here from ConceptoPagoRepository for better responsibility separation.
     * @param idDetalle The ID of the payroll detail.
     * @return A list of MovimientoPlanilla for the specified payroll detail ID.
     */
    @Query("SELECT mp FROM MovimientoPlanilla mp WHERE mp.detallePlanilla.idDetalle = :idDetalle")
    List<MovimientoPlanilla> findByDetallePlanillaId(Integer idDetalle); // ID is Integer
}
