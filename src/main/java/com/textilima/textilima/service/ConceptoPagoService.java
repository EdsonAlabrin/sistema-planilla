package com.textilima.textilima.service;

import com.textilima.textilima.service.ConceptoPagoService;
import com.textilima.textilima.entities.ConceptoPago;
import com.textilima.textilima.entities.ConceptoPago.TipoConcepto; // Importar TipoConcepto

import java.util.List;
import java.util.Optional;

// Interfaz que define los contratos del servicio de Conceptos de Pago
public interface ConceptoPagoService {

    // Obtiene todos los conceptos de pago
    List<ConceptoPago> findAll();

    // Busca un concepto de pago por su ID
    Optional<ConceptoPago> findById(Integer id);

    // Guarda un nuevo concepto de pago o actualiza uno existente
    ConceptoPago save(ConceptoPago conceptoPago);

    // Elimina un concepto de pago por su ID
    void deleteById(Integer id);

    // Busca un concepto de pago por su nombre (útil para validaciones de unicidad)
    Optional<ConceptoPago> findByNombreConcepto(String nombreConcepto);

    // Opcional: Podríamos añadir métodos para buscar por tipo de concepto si fuera necesario
    List<ConceptoPago> findByTipo(TipoConcepto tipo);
}
