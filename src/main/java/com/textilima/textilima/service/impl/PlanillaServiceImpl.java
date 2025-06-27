package com.textilima.textilima.service.impl;

// Importaciones de interfaces de repositorio - ¡VERIFICA SUS PAQUETES!
import com.textilima.textilima.repository.PlanillaRepository;
import com.textilima.textilima.model.Planilla;
import com.textilima.textilima.model.Planilla.TipoPlanilla;
// Importaciones de interfaces de servicio - ¡VERIFICA SUS PAQUETES!
import com.textilima.textilima.service.PlanillaService;
// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PlanillaServiceImpl implements PlanillaService {

   @Autowired // Injects the PlanillaRepository dependency
    private PlanillaRepository planillaRepository;

    /**
     * Retrieves a list of all payrolls.
     * @return A list of Planilla objects.
     */
    @Override
    public List<Planilla> getAllPlanillas() {
        return planillaRepository.findAll();
    }

    /**
     * Retrieves a payroll by its ID.
     * @param id The ID of the payroll.
     * @return An Optional containing the Planilla if found, or empty if not found.
     */
    @Override
    public Optional<Planilla> getPlanillaById(Integer idPlanilla) {
        return planillaRepository.findById(idPlanilla);
    }

    /**
     * Saves a new payroll or updates an existing one.
     * @param planilla The Planilla object to save/update.
     * @return The saved/updated Planilla.
     */
    @Override
    public Planilla savePlanilla(Planilla planilla) {
        // Here you could add additional business logic before saving,
        // for example, validating uniqueness of month/year/type combination.
        return planillaRepository.save(planilla);
    }

    /**
     * Deletes a payroll by its ID.
     * @param id The ID of the payroll to delete.
     */
    @Override
    public void deletePlanilla(Integer idPlanilla) {
        planillaRepository.deleteById(idPlanilla);
    }

    /**
     * Searches for a payroll by month, year, and type.
     * @param mes The month of the payroll.
     * @param anio The year of the payroll.
     * @param tipoPlanilla The type of payroll (e.g., MENSUAL, CTS, GRATIFICACION).
     * @return An Optional containing the found Planilla, or empty if not found.
     */
    @Override
    public Optional<Planilla> getPlanillaByMesAnioAndTipo(Integer mes, Integer anio, TipoPlanilla tipoPlanilla) {
        return planillaRepository.findByMesAndAnioAndTipoPlanilla(mes, anio, tipoPlanilla);
    }

    /**
     * Searches for all payrolls generated within a date range.
     * @param fechaInicio The start date of the range (inclusive).
     * @param fechaFin The end date of the range (inclusive).
     * @return A list of payrolls generated within the specified range.
     */
    @Override
    public List<Planilla> getPlanillasByFechaGeneradaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return planillaRepository.findByFechaGeneradaBetween(fechaInicio, fechaFin);
    }

    /**
     * Searches for all payrolls of a specific type for a given year.
     * @param anio The year of the payroll.
     * @param tipoPlanilla The type of payroll.
     * @return A list of payrolls matching the year and type.
     */
    @Override
    public List<Planilla> getPlanillasByAnioAndTipo(Integer anio, TipoPlanilla tipoPlanilla) {
        return planillaRepository.findByAnioAndTipoPlanilla(anio, tipoPlanilla);
    }

    /*
     * Lógica compleja de cálculo de planilla (a implementar más adelante):
     *
     * Este método es el corazón del sistema y requerirá la interacción con varias entidades:
     * Empleado, Puesto, Asistencia, ConceptoPago, ParametroLegal, DetallePlanilla, MovimientoPlanilla.
     *
     * public Planilla generatePlanilla(Integer mes, Integer anio, TipoPlanilla tipoPlanilla) {
     * // 1. Validar si la planilla para ese mes/anio/tipo ya existe.
     * // 2. Obtener los empleados activos para el periodo.
     * // 3. Obtener los parámetros legales vigentes (RMV, UIT) para el periodo.
     * // 4. Para cada empleado:
     * //    a. Obtener su puesto actual y salario base.
     * //    b. Obtener sus asistencias (tardanzas, ausencias) para calcular descuentos.
     * //    c. Calcular asignación familiar si aplica.
     * //    d. Calcular otros ingresos (bonos, horas extras) según ConceptoPago.
     * //    e. Calcular descuentos (ONP/AFP, Impuesto a la Renta, otros) según ConceptoPago.
     * //    f. Calcular aportes del empleador (EsSalud, SCTR, etc.).
     * //    g. Crear un DetallePlanilla para el empleado con todos los totales.
     * //    h. Crear MovimientoPlanilla para cada ingreso/descuento/aporte.
     * // 5. Guardar la Planilla principal y todos sus Detalles y Movimientos.
     * // 6. Retornar la planilla generada.
     * }
     */
}
