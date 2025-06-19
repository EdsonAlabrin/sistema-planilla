package com.textilima.textilima.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.textilima.textilima.entities.Bono;
import com.textilima.textilima.entities.DetallePlanilla;

@Repository
public interface BonoRepository extends JpaRepository<Bono, Integer> {
   // Método para encontrar bonos por su detalle de planilla asociado
    List<Bono> findByDetallePlanilla(DetallePlanilla detallePlanilla);
}
