package com.textilima.textilima.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textilima.textilima.entities.ConceptoPago;
import com.textilima.textilima.entities.ConceptoPago.TipoConcepto;

public interface ConceptoPagoRepository extends JpaRepository<ConceptoPago, Integer> {
    // Método para buscar un ConceptoPago por su nombre (para validación de unicidad)
    Optional<ConceptoPago> findByNombreConcepto(String nombreConcepto);

    // ¡MÉTODO AÑADIDO/CORREGIDO!
    // Busca una lista de conceptos de pago por su tipo (INGRESO, DESCUENTO, etc.)
    List<ConceptoPago> findByTipo(TipoConcepto tipo);
}
