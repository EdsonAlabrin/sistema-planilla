package com.textilima.textilima.service.impl;

import com.textilima.textilima.entities.ConceptoPago;
import com.textilima.textilima.entities.ConceptoPago.TipoConcepto;
import com.textilima.textilima.repository.ConceptoPagoRepository;
import com.textilima.textilima.service.ConceptoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como un componente de servicio de Spring
public class ConceptoPagoServiceImpl implements ConceptoPagoService {

    private final ConceptoPagoRepository conceptoPagoRepository;

    @Autowired // Inyección de dependencias a través del constructor
    public ConceptoPagoServiceImpl(ConceptoPagoRepository conceptoPagoRepository) {
        this.conceptoPagoRepository = conceptoPagoRepository;
    }

    @Override
    @Transactional(readOnly = true) // La operación de lectura no modifica la base de datos
    public List<ConceptoPago> findAll() {
        return conceptoPagoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConceptoPago> findById(Integer id) {
        return conceptoPagoRepository.findById(id);
    }

    @Override
    @Transactional // La operación de guardar/actualizar modifica la base de datos
    public ConceptoPago save(ConceptoPago conceptoPago) {
        // En una aplicación real, aquí podrías añadir validaciones más complejas,
        // por ejemplo, verificar si el 'codigoSunat' ya existe si se requiere unicidad.
        return conceptoPagoRepository.save(conceptoPago);
    }

    @Override
    @Transactional // La operación de eliminar modifica la base de datos
    public void deleteById(Integer id) {
        conceptoPagoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConceptoPago> findByNombreConcepto(String nombreConcepto) {
        return conceptoPagoRepository.findByNombreConcepto(nombreConcepto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConceptoPago> findByTipo(TipoConcepto tipo) {
        return conceptoPagoRepository.findByTipo(tipo);
    }
}
