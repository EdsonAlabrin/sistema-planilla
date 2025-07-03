package com.textilima.textilima.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;

@Repository
public interface ConceptoPagoRepository extends JpaRepository<ConceptoPago, Integer> {
    /**
     * Busca un concepto de pago por su nombre.
     * @param nombreConcepto The name of the payment concept to search for.
     * @return An Optional containing the found ConceptoPago, or empty if it does not exist.
     */
    Optional<ConceptoPago> findByNombreConcepto(String nombreConcepto);

    /**
     * Busca un concepto de pago por su nombre y tipo.
     * @param nombreConcepto The name of the payment concept.
     * @param tipo The type of the concept (INGRESO, DESCUENTO, APORTE_EMPLEADOR).
     * @return An Optional containing the ConceptoPago found, or empty if it does not exist.
     */
    Optional<ConceptoPago> findByNombreConceptoAndTipo(String nombreConcepto, TipoConcepto tipo); // ADDED for controller

    /**
     * Busca todos los conceptos de pago de un tipo específico (INGRESO, DESCUENTO, APORTE_EMPLEADOR).
     * @param tipo The type of concept to search for.
     * @return A list of ConceptoPago that match the specified type.
     */
    List<ConceptoPago> findByTipo(TipoConcepto tipo);

    /**
     * Busca conceptos de pago que sean afectos a ONP, AFP o EsSalud.
     * @param afectoOnp True if it is subject to ONP.
     * @param afectoAfp True if it is subject to AFP.
     * @param afectoEssalud True if it is subject to EsSalud.
     * @return A list of ConceptoPago that meet the affection conditions.
     */
    List<ConceptoPago> findByAfectoOnpOrAfectoAfpOrAfectoEssalud(Boolean afectoOnp, Boolean afectoAfp, Boolean afectoEssalud);

    // Nuevo método para buscar por nombre y tipo
  
}
