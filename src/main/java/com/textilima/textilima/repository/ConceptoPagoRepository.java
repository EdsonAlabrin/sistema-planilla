package com.textilima.textilima.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;

public interface ConceptoPagoRepository extends JpaRepository<ConceptoPago, Integer> {
    /**
     * Busca un concepto de pago por su nombre.
     * @param nombreConcepto El nombre del concepto de pago a buscar.
     * @return Un Optional que contiene el ConceptoPago encontrado, o vacío si no existe.
     */
    Optional<ConceptoPago> findByNombreConcepto(String nombreConcepto);

    /**
     * Busca todos los conceptos de pago de un tipo específico (INGRESO, DESCUENTO, APORTE_EMPLEADOR).
     * @param tipo El tipo de concepto a buscar.
     * @return Una lista de ConceptoPago que coinciden con el tipo especificado.
     */
    List<ConceptoPago> findByTipo(TipoConcepto tipo);

    /**
     * Busca conceptos de pago que sean afectos a ONP, AFP o EsSalud.
     * @param afectoOnp True si es afecto a ONP.
     * @param afectoAfp True si es afecto a AFP.
     * @param afectoEssalud True si es afecto a EsSalud.
     * @return Una lista de ConceptoPago que cumplen con las condiciones de afectación.
     */
    List<ConceptoPago> findByAfectoOnpOrAfectoAfpOrAfectoEssalud(Boolean afectoOnp, Boolean afectoAfp, Boolean afectoEssalud);
}
