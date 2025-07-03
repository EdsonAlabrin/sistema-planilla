package com.textilima.textilima.service.impl;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;
import com.textilima.textilima.repository.ConceptoPagoRepository;
import com.textilima.textilima.service.ConceptoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class ConceptoPagoServiceImpl implements ConceptoPagoService {

    @Autowired // Injects the ConceptoPagoRepository dependency
    private ConceptoPagoRepository conceptoPagoRepository;

    /**
     * Retrieves a list of all payment concepts.
     * @return A list of ConceptoPago objects.
     */
    @Override
    public List<ConceptoPago> getAllConceptosPago() {
        return conceptoPagoRepository.findAll();
    }

    /**
     * Retrieves a payment concept by its ID.
     * @param id The ID of the payment concept.
     * @return An Optional containing the ConceptoPago if found, or empty if not found.
     */
    @Override
    public Optional<ConceptoPago> getConceptoPagoById(Integer idConcepto) {
        return conceptoPagoRepository.findById(idConcepto);
    }

    /**
     * Saves a new payment concept or updates an existing one.
     * @param conceptoPago The ConceptoPago object to save/update.
     * @return The saved/updated ConceptoPago.
     */
    @Override
    public ConceptoPago saveConceptoPago(ConceptoPago conceptoPago) {
        // Here you could add additional business logic before saving,
        // for example, validating uniqueness of concept name or code.
        return conceptoPagoRepository.save(conceptoPago);
    }

    /**
     * Deletes a payment concept by its ID.
     * @param id The ID of the payment concept to delete.
     */
    @Override
    public void deleteConceptoPago(Integer idConcepto) {
        conceptoPagoRepository.deleteById(idConcepto);
    }

    /**
     * Searches for a payment concept by its name.
     * @param nombreConcepto The name of the payment concept to search for.
     * @return An Optional containing the found ConceptoPago, or empty if not found.
     */
    @Override
    public Optional<ConceptoPago> getConceptoPagoByNombre(String nombreConcepto) {
        return conceptoPagoRepository.findByNombreConcepto(nombreConcepto);
    }

    /**
     * Searches for all payment concepts of a specific type (INCOME, DEDUCTION, EMPLOYER_CONTRIBUTION).
     * @param tipo The type of concept to search for.
     * @return A list of ConceptoPago matching the specified type.
     */
    @Override
    public List<ConceptoPago> getConceptosPagoByTipo(TipoConcepto tipo) {
        return conceptoPagoRepository.findByTipo(tipo);
    }

    /**
     * Searches for payment concepts that are affected by ONP, AFP, or EsSalud.
     * @param afectoOnp True if affected by ONP.
     * @param afectoAfp True if affected by AFP.
     * @param afectoEssalud True if affected by EsSalud.
     * @return A list of ConceptoPago that meet the affection conditions.
     */
    @Override
    public List<ConceptoPago> getConceptosPagoByAfectacion(Boolean afectoOnp, Boolean afectoAfp, Boolean afectoEssalud) {
        return conceptoPagoRepository.findByAfectoOnpOrAfectoAfpOrAfectoEssalud(afectoOnp, afectoAfp, afectoEssalud);
    }

    /**
     * Implementación del método para buscar un ConceptoPago por su nombre y tipo.
     * Se delega la búsqueda al repositorio.
     */
    @Override
    public Optional<ConceptoPago> getConceptoPagoByNombreAndTipo(String nombreConcepto, TipoConcepto tipo) {
        return conceptoPagoRepository.findByNombreConceptoAndTipo(nombreConcepto, tipo);
    }
}
