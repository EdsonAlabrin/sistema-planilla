package com.textilima.textilima.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.ParametroLegal;
import com.textilima.textilima.repository.ParametroLegalRepository;
import com.textilima.textilima.service.ParametroLegalService;


@Service
public class ParametroLegalServiceImpl implements ParametroLegalService {
    private final ParametroLegalRepository parametroLegalRepository;

    @Autowired
    public ParametroLegalServiceImpl(ParametroLegalRepository parametroLegalRepository) {
        this.parametroLegalRepository = parametroLegalRepository;
    }

    @Override
    public List<ParametroLegal> getAllParametrosLegales() {
        return parametroLegalRepository.findAll();
    }

    @Override
    public Optional<ParametroLegal> getParametroLegalById(Integer id) {
        return parametroLegalRepository.findById(id);
    }

    @Override
    public ParametroLegal saveParametroLegal(ParametroLegal parametroLegal) {
        if (parametroLegal.getFechaFinVigencia() != null && parametroLegal.getFechaInicioVigencia().isAfter(parametroLegal.getFechaFinVigencia())) {
            throw new IllegalArgumentException("La fecha de inicio de vigencia no puede ser posterior a la fecha de fin de vigencia.");
        }
        return parametroLegalRepository.save(parametroLegal);
    }

    @Override
    public void deleteParametroLegal(Integer id) {
        parametroLegalRepository.deleteById(id);
    }

    @Override
    public Optional<ParametroLegal> getParametroLegalByCodigoAndFechaVigencia(String codigo, LocalDate fecha) { // <-- IMPLEMENTACIÓN DEL NOMBRE CORRECTO
        return parametroLegalRepository.findVigenteByCodigoAndFecha(codigo, fecha);
    }

    @Override
    public List<ParametroLegal> findByCodigo(String codigo) {
        return parametroLegalRepository.findByCodigo(codigo);
    }

    @Override
    public List<ParametroLegal> findByFechaVigenciaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return parametroLegalRepository.findByFechaInicioVigenciaBetween(fechaInicio, fechaFin);
    }
}
