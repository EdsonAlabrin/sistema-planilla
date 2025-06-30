package com.textilima.textilima.service.impl;

import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Planilla;
import com.textilima.textilima.repository.DetallePlanillaRepository;
import com.textilima.textilima.service.DetallePlanillaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DetallePlanillaServiceImpl implements DetallePlanillaService {
    
    private static final Logger logger = LoggerFactory.getLogger(DetallePlanillaServiceImpl.class);

    // Repository for accessing DetallePlanilla data
   @Autowired // Injects the DetallePlanillaRepository dependency
    private DetallePlanillaRepository detallePlanillaRepository;

    /**
     * Retrieves a list of all payroll details.
     * @return A list of DetallePlanilla objects.
     */
    @Override
    public List<DetallePlanilla> getAllDetallesPlanilla() {
        return detallePlanillaRepository.findAll();
    }

    /**
     * Retrieves a payroll detail by its ID.
     * @param id The ID of the payroll detail.
     * @return An Optional containing the DetallePlanilla if found, or empty if not found.
     */
    @Override
    public Optional<DetallePlanilla> getDetallePlanillaById(Integer idDetalle) {
        return detallePlanillaRepository.findById(idDetalle);
    }

    /**
     * Saves a new payroll detail or updates an existing one.
     * @param detallePlanilla The DetallePlanilla object to save/update.
     * @return The saved/updated DetallePlanilla.
     */
    @Override
    public DetallePlanilla saveDetallePlanilla(DetallePlanilla detallePlanilla) {
        // Here you could add additional business logic before saving,
        // such as recalculating totals based on associated movements.
        return detallePlanillaRepository.save(detallePlanilla);
    }

    /**
     * Deletes a payroll detail by its ID.
     * @param id The ID of the payroll detail to delete.
     */
    @Override
    public void deleteDetallePlanilla(Integer idDetalle) {
        detallePlanillaRepository.deleteById(idDetalle);
    }

    /**
     * Searches for a specific payroll detail by the payroll and employee.
     * @param planilla The payroll to which the detail belongs.
     * @param empleado The employee to whom the detail corresponds.
     * @return An Optional containing the found DetallePlanilla, or empty if not found.
     */
    @Override
    public Optional<DetallePlanilla> getDetallePlanillaByPlanillaAndEmpleado(Planilla planilla, Empleado empleado) {
        return detallePlanillaRepository.findByPlanillaAndEmpleado(planilla, empleado);
    }

    /**
     * Searches for all details of a specific payroll.
     * @param planilla The payroll for which details are desired.
     * @return A list of DetallePlanilla for the specified payroll.
     */
    @Override
    public List<DetallePlanilla> getDetallesByPlanilla(Planilla planilla) { // <--- ESTE ES EL MÉTODO
        logger.debug("Buscando detalles para la planilla ID: {}", planilla.getIdPlanilla());
        return detallePlanillaRepository.findByPlanilla(planilla);
    }

    /**
     * Searches for all payroll details of a particular employee.
     * @param empleado The employee for whom payroll details are desired.
     * @return A list of DetallePlanilla for the specified employee.
     */
    @Override
    public List<DetallePlanilla> getDetallesPlanillaByEmpleado(Empleado empleado) {
        return detallePlanillaRepository.findByEmpleado(empleado);
    }

    /*
     * Lógica para calcular y actualizar los campos totales en DetallePlanilla
     * (por ejemplo, después de que los MovimientoPlanilla sean creados/actualizados):
     *
     * public BigDecimal calcularRemuneracionComputableAfecta(DetallePlanilla detalle) {
     * // Implementar la lógica de cálculo aquí.
     * return BigDecimal.ZERO; // Placeholder
     * }
     *
     * public BigDecimal calcularSueldoBruto(DetallePlanilla detalle) {
     * // Implementar la lógica de cálculo aquí.
     * return BigDecimal.ZERO; // Placeholder
     * }
     *
     * public BigDecimal calcularSueldoNeto(DetallePlanilla detalle) {
     * // Implementar la lógica de cálculo aquí.
     * return BigDecimal.ZERO; // Placeholder
     * }
     */
}
