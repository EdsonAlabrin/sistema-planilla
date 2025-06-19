package com.textilima.textilima.repository;

import com.textilima.textilima.entities.Planilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanillaRepository extends JpaRepository<Planilla, Integer> {
  
  // Busca una planilla por mes y año.
  Optional<Planilla> findByMesAndAnio(Integer mes, Integer anio);

}

