package com.textilima.textilima.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textilima.textilima.model.ParametroLegal;

public interface ParametroLegalRepository extends JpaRepository<ParametroLegal, Integer> {
     /**
     * Busca el parámetro legal más reciente por su código y una fecha de vigencia.
     * Retorna el parámetro que esté vigente en o antes de la fecha especificada,
     * priorizando el más reciente.
     *
     * @param codigo El código del parámetro legal (ej. "RMV", "UIT").
     * @param fechaVigencia La fecha para la cual se busca el parámetro vigente.
     * @return Un Optional que contiene el ParametroLegal si se encuentra uno vigente, o vacío.
     */
    Optional<ParametroLegal> findLatestByCodigoAndFechaVigenciaInicioLessThanEqual(
            String codigo, LocalDate fechaVigencia);
}
