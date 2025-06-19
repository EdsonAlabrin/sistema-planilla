package com.textilima.textilima.repository;

import com.textilima.textilima.entities.DetallePlanilla;
import com.textilima.textilima.entities.Empleado;
import com.textilima.textilima.entities.Planilla;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetallePlanillaRepository extends JpaRepository<DetallePlanilla, Integer> {
   // Método para encontrar detalles de planilla por el ID de la Planilla principal
    List<DetallePlanilla> findByPlanilla_IdPlanilla(Integer idPlanilla);

    // Método para encontrar detalles de planilla por el ID del Empleado
    List<DetallePlanilla> findByEmpleado_IdEmpleado(Integer idEmpleado);

    // ¡¡¡CORRECCIÓN AQUÍ!!! Usar 'And' en camel case para combinar condiciones
    // Este método busca un DetallePlanilla específico para una Planilla y un Empleado dados.
    Optional<DetallePlanilla> findByPlanillaAndEmpleado(Planilla planilla, Empleado empleado);
}

