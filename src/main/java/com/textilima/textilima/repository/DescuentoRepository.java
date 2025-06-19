package com.textilima.textilima.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.textilima.textilima.entities.Descuento;
import com.textilima.textilima.entities.DetallePlanilla;

public interface DescuentoRepository extends JpaRepository<Descuento, Integer> {

   // Método para encontrar descuentos por su detalle de planilla asociado
    List<Descuento> findByDetallePlanilla(DetallePlanilla detallePlanilla);
}
