package com.textilima.textilima.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.ControlVacacion;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.repository.ControlVacacionRepository;
import com.textilima.textilima.service.ControlVacacionService;

@Service
public class ControlVacacionServiceImpl implements ControlVacacionService {
    @Autowired // Injects the ControlVacacionRepository dependency
    private ControlVacacionRepository controlVacacionRepository;

    /**
     * Retrieves a list of all vacation control records.
     * @return A list of ControlVacacion objects.
     */
    @Override
    public List<ControlVacacion> getAllControlVacaciones() {
        return controlVacacionRepository.findAll();
    }

    /**
     * Retrieves a vacation control record by its ID.
     * @param id The ID of the vacation control record.
     * @return An Optional containing the ControlVacacion if found, or empty if not found.
     */
    @Override
    public Optional<ControlVacacion> getControlVacacionById(Integer idControlVac) {
        return controlVacacionRepository.findById(idControlVac);
    }

    /**
     * Saves a new vacation control record or updates an existing one.
     * @param controlVacacion The ControlVacacion object to save/update.
     * @return The saved/updated ControlVacacion.
     */
    @Override
    public ControlVacacion saveControlVacacion(ControlVacacion controlVacacion) {
        // Here you could add additional business logic before saving,
        // for example, validating period uniqueness or recalculating pending days.
        return controlVacacionRepository.save(controlVacacion);
    }

    /**
     * Deletes a vacation control record by its ID.
     * @param id The ID of the vacation control record to delete.
     */
    @Override
    public void deleteControlVacacion(Integer idControlVac) {
        controlVacacionRepository.deleteById(idControlVac);
    }

    /**
     * Searches for a vacation control record for a specific employee and period.
     * @param empleado The employee.
     * @param periodo The vacation period (e.g., "2023-2024").
     * @return An Optional containing the found ControlVacacion, or empty if not found.
     */
    @Override
    public Optional<ControlVacacion> getControlVacacionByEmpleadoAndPeriodo(Empleado empleado, String periodo) {
        return controlVacacionRepository.findByEmpleadoAndPeriodo(empleado, periodo);
    }

    /**
     * Searches for all vacation control records for an employee.
     * @param empleado The employee.
     * @return A list of ControlVacacion records for the employee.
     */
    @Override
    public List<ControlVacacion> getControlVacacionesByEmpleado(Empleado empleado) {
        return controlVacacionRepository.findByEmpleado(empleado);
    }

    /**
     * Searches for vacation control records where pending days are greater than zero
     * for a specific employee.
     * @param empleado The employee.
     * @return A list of ControlVacacion with pending days.
     */
    @Override
    public List<ControlVacacion> getControlVacacionesWithPendingDaysByEmpleado(Empleado empleado) {
        return controlVacacionRepository.findByEmpleadoAndDiasPendientesGreaterThan(empleado, 0);
    }
}
