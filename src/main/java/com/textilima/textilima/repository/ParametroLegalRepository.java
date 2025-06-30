package com.textilima.textilima.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.ParametroLegal;

@Repository
public interface ParametroLegalRepository extends JpaRepository<ParametroLegal, Integer> {
   // Busca un parámetro legal por su código y que esté vigente en una fecha dada.
    @Query("SELECT pl FROM ParametroLegal pl WHERE pl.codigo = :codigo AND pl.fechaInicioVigencia <= :fecha " +
           "AND (pl.fechaFinVigencia IS NULL OR pl.fechaFinVigencia >= :fecha)")
    Optional<ParametroLegal> findVigenteByCodigoAndFecha(@Param("codigo") String codigo, @Param("fecha") LocalDate fecha);

    // Método para obtener el valor más reciente de un parámetro por su código
    Optional<ParametroLegal> findTopByCodigoOrderByFechaInicioVigenciaDesc(String codigo); // ADICIONADO

    List<ParametroLegal> findByCodigo(String codigo);
    List<ParametroLegal> findByDescripcionContainingIgnoreCase(String descripcion);
    List<ParametroLegal> findByFechaInicioVigenciaBetween(LocalDate startDate, LocalDate endDate);
}
