package com.textilima.textilima.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Boleta;
import com.textilima.textilima.model.DetallePlanilla;

@Repository
public interface BoletaRepository  extends JpaRepository<Boleta, Integer> {
    /**
     * Busca una boleta por su detalle de planilla asociado.
     * Dado que la relación es OneToOne, debería retornar un único resultado.
     * @param detallePlanilla El detalle de planilla al que está asociada la boleta.
     * @return Un Optional que contiene la Boleta encontrada, o vacío si no existe.
     */
    Optional<Boleta> findByDetallePlanilla(DetallePlanilla detallePlanilla);

    /**
     * Busca boletas emitidas en un rango de fechas.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin La fecha de fin del rango (inclusive).
     * @return Una lista de Boleta emitidas en el rango especificado.
     */
    List<Boleta> findByFechaEmisionBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca boletas que han sido firmadas.
     * @param firmada Booleano que indica si la boleta está firmada (true) o no (false).
     * @return Una lista de Boleta que cumplen con la condición de firma.
     */
    List<Boleta> findByFirmada(Boolean firmada);

    /**
     * Busca boletas que han sido enviadas.
     * @param enviada Booleano que indica si la boleta ha sido enviada (true) o no (false).
     * @return Una lista de Boleta que cumplen con la condición de envío.
     */
    List<Boleta> findByEnviada(Boolean enviada);
}
