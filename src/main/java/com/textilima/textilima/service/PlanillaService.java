package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;
import com.textilima.textilima.entities.Planilla;

// Interfaz que define los contratos del servicio de planillas
public interface PlanillaService {
     Planilla generarPlanillaMensual(Integer mes, Integer anio);
    List<Planilla> getAllPlanillas();
    Optional<Planilla> getPlanillaById(Integer id);
    Optional<Planilla> findByMesAndAnio(Integer mes, Integer anio);
    void deleteById(Integer id);
}

