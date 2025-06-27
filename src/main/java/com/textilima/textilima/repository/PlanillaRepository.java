package com.textilima.textilima.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Planilla;
import com.textilima.textilima.model.Planilla.TipoPlanilla;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanillaRepository extends JpaRepository<Planilla, Integer> {
  
  /**
     * Busca una planilla por mes, año y tipo.
     * @param mes El mes de la planilla.
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla (ej. MENSUAL, CTS, GRATIFICACION).
     * @return Un Optional que contiene la Planilla encontrada, o vacío si no existe.
     */
    Optional<Planilla> findByMesAndAnioAndTipoPlanilla(Integer mes, Integer anio, TipoPlanilla tipoPlanilla);

    /**
     * Busca todas las planillas generadas en un rango de fechas.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin La fecha de fin del rango (inclusive).
     * @return Una lista de planillas generadas en el rango especificado.
     */
    List<Planilla> findByFechaGeneradaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca todas las planillas de un tipo específico para un año dado.
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla.
     * @return Una lista de planillas que coinciden con el año y tipo.
     */
    List<Planilla> findByAnioAndTipoPlanilla(Integer anio, TipoPlanilla tipoPlanilla);

}

