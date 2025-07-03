package com.textilima.textilima.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.HistorialPuesto;
import com.textilima.textilima.repository.HistorialPuestoRepository;
import com.textilima.textilima.service.HistorialPuestoService;


@Service
public class HistorialPuestoServiceImpl implements HistorialPuestoService {
    @Autowired // Injects the HistorialPuestoRepository dependency
    private HistorialPuestoRepository historialPuestoRepository;

    /**
     * Retrieves a list of all historical job records.
     * @return A list of HistorialPuesto objects.
     */
    @Override
    public List<HistorialPuesto> getAllHistorialPuestos() {
        return historialPuestoRepository.findAll();
    }

    /**
     * Retrieves a historical job record by its ID.
     * @param id The ID of the historical job record.
     * @return An Optional containing the HistorialPuesto if found, or empty if not found.
     */
    @Override
    public Optional<HistorialPuesto> getHistorialPuestoById(Integer idHistorial) {
        return historialPuestoRepository.findById(idHistorial);
    }

    /**
     * Saves a new historical job record or updates an existing one.
     * @param historialPuesto The HistorialPuesto object to save/update.
     * @return The saved/updated HistorialPuesto.
     */
    @Override
    public HistorialPuesto saveHistorialPuesto(HistorialPuesto historialPuesto) {
        // Here you could add additional business logic before saving,
        // for example, validating date overlaps or ensuring a 'current' position exists.
        return historialPuestoRepository.save(historialPuesto);
    }

    /**
     * Deletes a historical job record by its ID.
     * @param id The ID of the historical job record to delete.
     */
    @Override
    public void deleteHistorialPuesto(Integer idHistorial) {
        historialPuestoRepository.deleteById(idHistorial);
    }

    /**
     * Searches for a specific employee's job history, ordered by start date descending.
     * @param empleado The employee to search history for.
     * @return A list of historical job records for the employee.
     */
    @Override
    public List<HistorialPuesto> getHistorialPuestosByEmpleado(Empleado empleado) {
        return historialPuestoRepository.findByEmpleadoOrderByFechaInicioDesc(empleado);
    }

    /**
     * Searches for an employee's current job (one with no end date or the latest end date).
     * @param empleado The employee to search the current job for.
     * @return An Optional containing the HistorialPuesto record of the current job, or empty if not found.
     */
    @Override
    public Optional<HistorialPuesto> getCurrentPuestoByEmpleado(Empleado empleado) {
        return historialPuestoRepository.findByEmpleadoAndFechaFinIsNull(empleado);
    }

   /**
     * Implementación que delega al repositorio para encontrar el puesto activo en una fecha dada.
     */
    @Override
    public Optional<HistorialPuesto> getPuestoByEmpleadoAndDate(Empleado empleado, LocalDate date) {
        // Llama al método del repositorio con la anotación @Query
        return historialPuestoRepository.findByEmpleadoAndDate(empleado, date);
    }

}
