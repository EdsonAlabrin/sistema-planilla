package com.textilima.textilima.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.MovimientoPlanilla;
import com.textilima.textilima.repository.MovimientoPlanillaRepository;
import com.textilima.textilima.service.MovimientoPlanillaService;

@Service
public class MovimientoPlanillaServiceImpl implements MovimientoPlanillaService {
    @Autowired // Injects the MovimientoPlanillaRepository dependency
    private MovimientoPlanillaRepository movimientoPlanillaRepository;

    /**
     * Retrieves a list of all payroll movements.
     * @return A list of MovimientoPlanilla objects.
     */
    @Override
    public List<MovimientoPlanilla> getAllMovimientosPlanilla() {
        return movimientoPlanillaRepository.findAll();
    }

    /**
     * Retrieves a payroll movement by its ID.
     * @param id The ID of the payroll movement.
     * @return An Optional containing the MovimientoPlanilla if found, or empty if not found.
     */
    @Override
    public Optional<MovimientoPlanilla> getMovimientoPlanillaById(Integer idMovimiento) {
        return movimientoPlanillaRepository.findById(idMovimiento);
    }

    /**
     * Saves a new payroll movement or updates an existing one.
     * @param movimientoPlanilla The MovimientoPlanilla object to save/update.
     * @return The saved/updated MovimientoPlanilla.
     */
    @Override
    public MovimientoPlanilla saveMovimientoPlanilla(MovimientoPlanilla movimientoPlanilla) {
        // Here you could add additional business logic before saving,
        // for example, ensuring the monto is positive or updating related DetallePlanilla totals.
        return movimientoPlanillaRepository.save(movimientoPlanilla);
    }

    /**
     * Deletes a payroll movement by its ID.
     * @param id The ID of the payroll movement to delete.
     */
    @Override
    public void deleteMovimientoPlanilla(Integer idMovimiento) {
        movimientoPlanillaRepository.deleteById(idMovimiento);
    }

    /**
     * Searches for all payroll movements associated with a specific payroll detail.
     * @param detallePlanilla The payroll detail to which the movements belong.
     * @return A list of MovimientoPlanilla for the specified payroll detail.
     */
    @Override
    public List<MovimientoPlanilla> getMovimientosByDetallePlanilla(DetallePlanilla detallePlanilla) {
        return movimientoPlanillaRepository.findByDetallePlanilla(detallePlanilla);
    }

    /**
     * Searches for a payroll movement by the payroll detail and the payment concept.
     * @param detallePlanilla The payroll detail.
     * @param concepto The payment concept.
     * @return An Optional containing the found MovimientoPlanilla, or empty.
     */
    @Override
    public Optional<MovimientoPlanilla> getMovimientoByDetallePlanillaAndConcepto(DetallePlanilla detallePlanilla, ConceptoPago concepto) {
        return movimientoPlanillaRepository.findByDetallePlanillaAndConcepto(detallePlanilla, concepto);
    }

    /**
     * Searches for payroll movements by a specific payment concept.
     * @param concepto The payment concept.
     * @return A list of MovimientoPlanilla that use the specified concept.
     */
    @Override
    public List<MovimientoPlanilla> getMovimientosByConcepto(ConceptoPago concepto) {
        return movimientoPlanillaRepository.findByConcepto(concepto);
    }
}
