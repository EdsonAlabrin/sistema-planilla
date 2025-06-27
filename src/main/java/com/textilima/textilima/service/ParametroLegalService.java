package com.textilima.textilima.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.ParametroLegal;

public interface ParametroLegalService {
    /**
     * Obtiene una lista de todos los parámetros legales.
     * @return Una lista de objetos ParametroLegal.
     */
    List<ParametroLegal> getAllParametrosLegales();

    /**
     * Obtiene un parámetro legal por su ID.
     * @param id El ID del parámetro legal.
     * @return Un Optional que contiene el ParametroLegal si se encuentra, o vacío si no existe.
     */
    Optional<ParametroLegal> getParametroLegalById(Integer idParametro);

    /**
     * Guarda un nuevo parámetro legal o actualiza uno existente.
     * @param parametroLegal El objeto ParametroLegal a guardar/actualizar.
     * @return El ParametroLegal guardado/actualizado.
     */
    ParametroLegal saveParametroLegal(ParametroLegal parametroLegal);

    /**
     * Elimina un parámetro legal por su ID.
     * @param id El ID del parámetro legal a eliminar.
     */
    void deleteParametroLegal(Integer idParametro);

    /**
     * Busca el parámetro legal más reciente por su código y una fecha de vigencia.
     * Retorna el parámetro que esté vigente en o antes de la fecha especificada,
     * priorizando el más reciente.
     * @param codigo El código del parámetro legal (ej. "RMV", "UIT").
     * @param fechaVigencia La fecha para la cual se busca el parámetro vigente.
     * @return Un Optional que contiene el ParametroLegal si se encuentra uno vigente, o vacío.
     */
    Optional<ParametroLegal> getLatestParametroLegalByCodigoAndFecha(String codigo, LocalDate fechaVigencia);
}
