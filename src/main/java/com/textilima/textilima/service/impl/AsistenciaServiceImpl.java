package com.textilima.textilima.service.impl;

import com.textilima.textilima.model.Asistencia;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.repository.AsistenciaRepository;
import com.textilima.textilima.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    @Autowired // Injects the AsistenciaRepository dependency
    private AsistenciaRepository asistenciaRepository;

    /**
     * Retrieves a list of all attendances.
     * @return A list of Asistencia objects.
     */
    @Override
    public List<Asistencia> getAllAsistencias() {
        return asistenciaRepository.findAll();
    }

    /**
     * Retrieves an attendance record by its ID.
     * @param id The ID of the attendance record.
     * @return An Optional containing the Asistencia if found, or empty if not found.
     */
    @Override
    public Optional<Asistencia> getAsistenciaById(Integer idAsistencia) {
        return asistenciaRepository.findById(idAsistencia);
    }

    /**
     * Saves a new attendance record or updates an existing one.
     * @param asistencia The Asistencia object to save/update.
     * @return The saved/updated Asistencia.
     */
    @Override
    public Asistencia saveAsistencia(Asistencia asistencia) {
        // Here you could add additional business logic before saving,
        // for example, calculating tardiness minutes based on entry time and expected time.
        return asistenciaRepository.save(asistencia);
    }

    /**
     * Deletes an attendance record by its ID.
     * @param id The ID of the attendance record to delete.
     */
    @Override
    public void deleteAsistencia(Integer idAsistencia) {
        asistenciaRepository.deleteById(idAsistencia);
    }

    /**
     * Searches for an employee's attendance for a specific date.
     * @param empleado The employee.
     * @param fecha The date of the attendance.
     * @return An Optional containing the found Asistencia, or empty if not found.
     */
    @Override
    public Optional<Asistencia> getAsistenciaByEmpleadoAndFecha(Empleado empleado, LocalDate fecha) {
        return asistenciaRepository.findByEmpleadoAndFecha(empleado, fecha);
    }

    /**
     * Searches for all attendance records of an employee within a date range.
     * @param empleado The employee.
     * @param fechaInicio The start date of the range (inclusive).
     * @param fechaFin The end date of the range (inclusive).
     * @return A list of attendance records for the employee within the specified range.
     */
    @Override
    public List<Asistencia> getAsistenciasByEmpleadoAndFechaBetween(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin) {
        return asistenciaRepository.findByEmpleadoAndFechaBetween(empleado, fechaInicio, fechaFin);
    }

    /**
     * Searches for attendance records with tardiness or absence status for an employee within a date range.
     * This is useful for calculating payroll deductions.
     * @param empleado The employee.
     * @param fechaInicio The start date of the range (inclusive).
     * @param fechaFin The end date of the range (inclusive).
     * @param estados The attendance statuses to search for (e.g., Asistencia.EstadoAsistencia.TARDANZA, Asistencia.EstadoAsistencia.AUSENTE).
     * @return A list of attendance records matching the statuses and date range.
     */
    @Override
    public List<Asistencia> getAsistenciasByEmpleadoAndFechaBetweenAndEstados(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin, List<Asistencia.EstadoAsistencia> estados) {
        return asistenciaRepository.findByEmpleadoAndFechaBetweenAndEstadoIn(empleado, fechaInicio, fechaFin, estados);
    }
}
