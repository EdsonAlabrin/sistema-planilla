package com.textilima.textilima.service;

import com.textilima.textilima.entities.Bono;
import com.textilima.textilima.entities.Descuento;
import com.textilima.textilima.entities.DetallePlanilla;

import java.util.List;
import java.util.Optional;

public interface DetallePlanillaService {
  // Métodos CRUD básicos
    DetallePlanilla save(DetallePlanilla detallePlanilla);
    Optional<DetallePlanilla> findById(Integer idDetalle);
    List<DetallePlanilla> findAll();
    void deleteById(Integer idDetalle);

    // Métodos específicos que usa el controlador
    List<DetallePlanilla> findByPlanillaId(Integer idPlanilla);
    List<DetallePlanilla> findByEmpleadoId(Integer idEmpleado); // Por si se usa en el servicio

    // Nuevos métodos para agregar bonos, descuentos y generar boletas
    DetallePlanilla agregarBonoADetalle(Integer idDetalle, Bono bono);
    DetallePlanilla agregarDescuentoADetalle(Integer idDetalle, Descuento descuento);
    DetallePlanilla generarBoleta(Integer idDetalle);
}