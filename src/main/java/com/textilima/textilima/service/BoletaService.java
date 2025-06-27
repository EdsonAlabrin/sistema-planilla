package com.textilima.textilima.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Boleta;
import com.textilima.textilima.model.DetallePlanilla;

public interface BoletaService {
    
    /**
     * Obtiene una lista de todas las boletas.
     * @return Una lista de objetos Boleta.
     */
    List<Boleta> getAllBoletas();

    /**
     * Obtiene una boleta por su ID.
     * @param id El ID de la boleta.
     * @return Un Optional que contiene la Boleta si se encuentra, o vacío si no existe.
     */
    Optional<Boleta> getBoletaById(Integer idBoleta);

    /**
     * Guarda una nueva boleta o actualiza una existente.
     * @param boleta El objeto Boleta a guardar/actualizar.
     * @return La Boleta guardada/actualizada.
     */
    Boleta saveBoleta(Boleta boleta);

    /**
     * Elimina una boleta por su ID.
     * @param id El ID de la boleta a eliminar.
     */
    void deleteBoleta(Integer idBoleta);

    /**
     * Busca una boleta por su detalle de planilla asociado.
     * @param detallePlanilla El detalle de planilla al que está asociada la boleta.
     * @return Un Optional que contiene la Boleta encontrada, o vacío si no existe.
     */
    Optional<Boleta> getBoletaByDetallePlanilla(DetallePlanilla detallePlanilla);

    /**
     * Busca boletas emitidas en un rango de fechas.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin La fecha de fin del rango (inclusive).
     * @return Una lista de Boleta emitidas en el rango especificado.
     */
    List<Boleta> getBoletasByFechaEmisionBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca boletas que han sido firmadas.
     * @param firmada Booleano que indica si la boleta está firmada (true) o no (false).
     * @return Una lista de Boleta que cumplen con la condición de firma.
     */
    List<Boleta> getBoletasByFirmadaStatus(Boolean firmada);

    /**
     * Busca boletas que han sido enviadas.
     * @param enviada Booleano que indica si la boleta ha sido enviada (true) o no (false).
     * @return Una lista de Boleta que cumplen con la condición de envío.
     */
    List<Boleta> getBoletasByEnviadaStatus(Boolean enviada);
}
