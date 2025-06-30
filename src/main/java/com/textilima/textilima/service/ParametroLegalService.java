package com.textilima.textilima.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.ParametroLegal;

public interface ParametroLegalService {
  List<ParametroLegal> getAllParametrosLegales();
    Optional<ParametroLegal> getParametroLegalById(Integer id);
    ParametroLegal saveParametroLegal(ParametroLegal parametroLegal);
    void deleteParametroLegal(Integer id);

    /**
     * Busca un parámetro legal por su código y que esté vigente en una fecha específica.
     * @param codigo El código del parámetro legal (ej. "RMV").
     * @param fecha La fecha para la cual el parámetro debe estar vigente.
     * @return Un Optional que contiene el ParametroLegal si se encuentra, o vacío si no.
     */
    Optional<ParametroLegal> getParametroLegalByCodigoAndFechaVigencia(String codigo, LocalDate fecha); // <-- NOMBRE CORRECTO

    List<ParametroLegal> findByCodigo(String codigo);
    List<ParametroLegal> findByFechaVigenciaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}
