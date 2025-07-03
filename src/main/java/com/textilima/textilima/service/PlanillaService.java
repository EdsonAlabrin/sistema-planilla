package com.textilima.textilima.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Planilla;
import com.textilima.textilima.model.Planilla.TipoPlanilla;

// Interfaz que define los contratos del servicio de planillas
public interface PlanillaService {
    
    /**
     * Obtiene una lista de todas las planillas.
     * @return Una lista de objetos Planilla.
     */
    List<Planilla> getAllPlanillas();

    /**
     * Obtiene una planilla por su ID.
     * @param id El ID de la planilla.
     * @return Un Optional que contiene la Planilla si se encuentra, o vacío si no existe.
     */
    Optional<Planilla> getPlanillaById(Integer idPlanilla);

    /**
     * Guarda una nueva planilla o actualiza una existente.
     * @param planilla El objeto Planilla a guardar/actualizar.
     * @return La Planilla guardada/actualizada.
     */
    Planilla savePlanilla(Planilla planilla);

    /**
     * Elimina una planilla por su ID.
     * @param id El ID de la planilla a eliminar.
     */
    void eliminarPlanillaCompleta(Integer idPlanilla); 

    /**
     * Busca una planilla por mes, año y tipo.
     * @param mes El mes de la planilla.
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla (ej. MENSUAL, CTS, GRATIFICACION).
     * @return Un Optional que contiene la Planilla encontrada, o vacío si no existe.
     */
    Optional<Planilla> getPlanillaByMesAnioAndTipo(Integer mes, Integer anio, TipoPlanilla tipoPlanilla);

    /**
     * Busca todas las planillas generadas en un rango de fechas.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin La fecha de fin del rango (inclusive).
     * @return Una lista de planillas generadas en el rango especificado.
     */
    List<Planilla> getPlanillasByFechaGeneradaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca todas las planillas de un tipo específico para un año dado.
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla.
     * @return Una lista de planillas que coinciden con el año y tipo.
     */
    List<Planilla> getPlanillasByAnioAndTipo(Integer anio, TipoPlanilla tipoPlanilla);

    /**
     * Genera y calcula una nueva planilla para un mes, año y tipo específicos.
     * Este es el método central para el cálculo de planillas.
     * @param mes El mes de la planilla (1-12).
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla a generar (MENSUAL, CTS, GRATIFICACION, LBS, VACACION).
     * @return La Planilla generada con todos sus detalles.
     * @throws IllegalStateException Si la planilla para el período y tipo ya existe.
     * @throws RuntimeException Si ocurren errores durante el cálculo.
     */
    Planilla generatePlanilla(Integer mes, Integer anio, TipoPlanilla tipoPlanilla);
    /**
     * Obtiene una planilla por su ID, cargando explícitamente sus detalles de planilla
     * y los movimientos de cada detalle.
     * @param idPlanilla El ID de la planilla a cargar.
     * @return La Planilla completa con sus detalles y movimientos.
     * @throws RuntimeException si la planilla no es encontrada.
     */
    Planilla getPlanillaWithDetails(Integer idPlanilla);

    
}

