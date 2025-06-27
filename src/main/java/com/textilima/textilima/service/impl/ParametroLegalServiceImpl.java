package com.textilima.textilima.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.textilima.textilima.model.ParametroLegal;
import com.textilima.textilima.repository.ParametroLegalRepository;
import com.textilima.textilima.service.ParametroLegalService;

public class ParametroLegalServiceImpl implements ParametroLegalService {
    @Autowired // Inyección de dependencia del repositorio de ParametroLegal
    private ParametroLegalRepository parametroLegalRepository;

    /**
     * Obtiene una lista de todos los parámetros legales.
     * @return Una lista de objetos ParametroLegal.
     */
    @Override
    public List<ParametroLegal> getAllParametrosLegales() {
        return parametroLegalRepository.findAll();
    }

    /**
     * Obtiene un parámetro legal por su ID.
     * @param id El ID del parámetro legal.
     * @return Un Optional que contiene el ParametroLegal si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<ParametroLegal> getParametroLegalById(Integer id) {
        return parametroLegalRepository.findById(id);
    }

    /**
     * Guarda un nuevo parámetro legal o actualiza uno existente.
     * @param parametroLegal El objeto ParametroLegal a guardar/actualizar.
     * @return El ParametroLegal guardado/actualizado.
     */
    @Override
    public ParametroLegal saveParametroLegal(ParametroLegal parametroLegal) {
        // Aquí se podría añadir lógica de negocio adicional antes de guardar,
        // por ejemplo, validaciones de unicidad de código y fecha de vigencia.
        return parametroLegalRepository.save(parametroLegal);
    }

    /**
     * Elimina un parámetro legal por su ID.
     * @param id El ID del parámetro legal a eliminar.
     */
    @Override
    public void deleteParametroLegal(Integer id) {
        parametroLegalRepository.deleteById(id);
    }

    /**
     * Busca el parámetro legal más reciente por su código y una fecha de vigencia.
     * Retorna el parámetro que esté vigente en o antes de la fecha especificada,
     * priorizando el más reciente.
     * @param codigo El código del parámetro legal (ej. "RMV", "UIT").
     * @param fechaVigencia La fecha para la cual se busca el parámetro vigente.
     * @return Un Optional que contiene el ParametroLegal si se encuentra uno vigente, o vacío.
     */
    @Override
    public Optional<ParametroLegal> getLatestParametroLegalByCodigoAndFecha(String codigo, LocalDate fechaVigencia) {
        return parametroLegalRepository.findLatestByCodigoAndFechaVigenciaInicioLessThanEqual(codigo, fechaVigencia);
    }
}
