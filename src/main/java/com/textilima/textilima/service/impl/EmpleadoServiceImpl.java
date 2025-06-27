package com.textilima.textilima.service.impl;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.repository.EmpleadoRepository; // Importa el repositorio de Empleado
import com.textilima.textilima.service.EmpleadoService; // Importa la interfaz de servicio de Empleado

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Anotación que marca esta clase como un componente de servicio de Spring
@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    
    @Autowired // Injects the EmpleadoRepository dependency
    private EmpleadoRepository empleadoRepository;

    /**
     * Retrieves a list of all employees.
     * @return A list of Empleado objects.
     */
    @Override
    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    /**
     * Retrieves an employee by their ID.
     * @param id The ID of the employee.
     * @return An Optional containing the Empleado if found, or empty if not found.
     */
    @Override
    public Optional<Empleado> getEmpleadoById(Integer idEmpleado) {
        return empleadoRepository.findById(idEmpleado);
    }

    /**
     * Saves a new employee or updates an existing one.
     * @param empleado The Empleado object to save/update.
     * @return The saved/updated Empleado.
     */
    @Override
    public Empleado saveEmpleado(Empleado empleado) {
        // Here you could add additional business logic before saving,
        // for example, validations for document uniqueness or date ranges.
        return empleadoRepository.save(empleado);
    }

    /**
     * Deletes an employee by their ID.
     * @param id The ID of the employee to delete.
     */
    @Override
    public void deleteEmpleado(Integer idEmpleado) {
        empleadoRepository.deleteById(idEmpleado);
    }

    /**
     * Searches for an employee by their document number (DNI, Foreigner's Card, etc.).
     * @param numeroDocumento The document number of the employee.
     * @return An Optional containing the found Empleado, or empty if not found.
     */
    @Override
    public Optional<Empleado> getEmpleadoByNumeroDocumento(String numeroDocumento) {
        return empleadoRepository.findByNumeroDocumento(numeroDocumento);
    }

    /**
     * Retrieves all employees who are currently active (estado = true).
     * @return A list of active employees.
     */
    @Override
    public List<Empleado> getActiveEmpleados() {
        return empleadoRepository.findByEstado(true);
    }

    /**
     * Retrieves all employees who have qualified children for family allowance.
     * @return A list of employees with qualified children.
     */
    @Override
    public List<Empleado> getEmpleadosWithCalifiedChildren() {
        return empleadoRepository.findByTieneHijosCalificados(true);
    }

    /**
     * Retrieves employees whose entry date is within a specified range.
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A list of employees who entered within the date range.
     */
    @Override
    public List<Empleado> getEmpleadosByFechaIngresoBetween(LocalDate startDate, LocalDate endDate) {
        return empleadoRepository.findByFechaIngresoBetween(startDate, endDate);
    }
}
