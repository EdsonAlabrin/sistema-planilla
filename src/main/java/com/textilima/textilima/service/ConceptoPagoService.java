package com.textilima.textilima.service;

import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;
import com.textilima.textilima.service.ConceptoPagoService;

import java.util.List;
import java.util.Optional;

// Interfaz que define los contratos del servicio de Conceptos de Pago
public interface ConceptoPagoService {

    /**
     * Obtiene una lista de todos los conceptos de pago.
     * @return Una lista de objetos ConceptoPago.
     */
    List<ConceptoPago> getAllConceptosPago();

    /**
     * Obtiene un concepto de pago por su ID.
     * @param id El ID del concepto de pago.
     * @return Un Optional que contiene el ConceptoPago si se encuentra, o vacío si no existe.
     */
    Optional<ConceptoPago> getConceptoPagoById(Integer idConcepto);

    /**
     * Guarda un nuevo concepto de pago o actualiza uno existente.
     * @param conceptoPago El objeto ConceptoPago a guardar/actualizar.
     * @return El ConceptoPago guardado/actualizado.
     */
    ConceptoPago saveConceptoPago(ConceptoPago conceptoPago);

    /**
     * Elimina un concepto de pago por su ID.
     * @param id El ID del concepto de pago a eliminar.
     */
    void deleteConceptoPago(Integer idConcepto);

    /**
     * Busca un concepto de pago por su nombre.
     * @param nombreConcepto El nombre del concepto de pago a buscar.
     * @return Un Optional que contiene el ConceptoPago encontrado, o vacío si no existe.
     */
    Optional<ConceptoPago> getConceptoPagoByNombre(String nombreConcepto);

    /**
     * Busca todos los conceptos de pago de un tipo específico (INGRESO, DESCUENTO, APORTE_EMPLEADOR).
     * @param tipo El tipo de concepto a buscar.
     * @return Una lista de ConceptoPago que coinciden con el tipo especificado.
     */
    List<ConceptoPago> getConceptosPagoByTipo(TipoConcepto tipo);

    /**
     * Busca conceptos de pago que sean afectos a ONP, AFP o EsSalud.
     * @param afectoOnp True si es afecto a ONP.
     * @param afectoAfp True si es afecto a AFP.
     * @param afectoEssalud True si es afecto a EsSalud.
     * @return Una lista de ConceptoPago que cumplen con las condiciones de afectación.
     */
    List<ConceptoPago> getConceptosPagoByAfectacion(Boolean afectoOnp, Boolean afectoAfp, Boolean afectoEssalud);
}
