// src/main/java/com/textilima/textilima/service/impl/EmpleadoServiceImpl.java
package com.textilima.textilima.service.impl;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.HistorialPuesto;
import com.textilima.textilima.model.Puesto;
import com.textilima.textilima.repository.EmpleadoRepository;
import com.textilima.textilima.repository.HistorialPuestoRepository;
import com.textilima.textilima.service.EmpleadoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar para transacciones

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    
    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private HistorialPuestoRepository historialPuestoRepository;

    @Override
    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    @Override
    public Optional<Empleado> getEmpleadoById(Integer idEmpleado) {
        return empleadoRepository.findById(idEmpleado);
    }

    @Override
    @Transactional
    public Empleado saveEmpleadoAndManagePuestoHistory(Empleado empleado) {
        boolean isNewEmployee = (empleado.getIdEmpleado() == null);
        
        Puesto oldPuesto = null;
        if (!isNewEmployee) {
            // Cargar el empleado existente para obtener el puesto actual antes de que sea sobrescrito
            Optional<Empleado> existingEmployeeOpt = empleadoRepository.findById(empleado.getIdEmpleado());
            if (existingEmployeeOpt.isPresent()) {
                Empleado existingEmployee = existingEmployeeOpt.get();
                oldPuesto = existingEmployee.getPuesto(); // Obtener el puesto actual
            }
        }

        // Guardar el empleado (esto actualizará el puesto directo del empleado si ha cambiado)
        Empleado savedEmpleado = empleadoRepository.save(empleado);

        // Lógica para gestionar el historial de puesto
        if (isNewEmployee) {
            // Si es un nuevo empleado, crear su primer registro en historial_puesto
            if (savedEmpleado.getPuesto() == null) {
                throw new RuntimeException("No se puede crear historial de puesto: El empleado no tiene un puesto asignado.");
            }
            HistorialPuesto nuevoHistorial = new HistorialPuesto();
            nuevoHistorial.setEmpleado(savedEmpleado);
            nuevoHistorial.setPuesto(savedEmpleado.getPuesto()); // El puesto recién asignado al empleado
            nuevoHistorial.setFechaInicio(savedEmpleado.getFechaIngreso()); // Fecha de ingreso del empleado
            nuevoHistorial.setFechaFin(null); // Puesto actual
            historialPuestoRepository.save(nuevoHistorial);
            System.out.println("DEBUG: Nuevo HistorialPuesto creado para empleado: " + savedEmpleado.getNombres() + " - Puesto: " + savedEmpleado.getPuesto().getNombrePuesto());

        } else if (savedEmpleado.getPuesto() != null && (oldPuesto == null || !savedEmpleado.getPuesto().equals(oldPuesto))) {
            // Si el empleado ya existe y su puesto ha cambiado
            System.out.println("DEBUG: Puesto de empleado " + savedEmpleado.getNombres() + " ha cambiado de " + (oldPuesto != null ? oldPuesto.getNombrePuesto() : "N/A") + " a " + savedEmpleado.getPuesto().getNombrePuesto());

            // Buscar el historial de puesto actualmente activo para este empleado
            Optional<HistorialPuesto> currentHistorialOpt = historialPuestoRepository.findByEmpleadoAndFechaFinIsNull(savedEmpleado);

            if (currentHistorialOpt.isPresent()) {
                HistorialPuesto currentHistorial = currentHistorialOpt.get();
                // Cerrar el historial de puesto anterior
                currentHistorial.setFechaFin(LocalDate.now().minusDays(1)); // Fecha de fin es el día anterior al cambio
                historialPuestoRepository.save(currentHistorial);
                System.out.println("DEBUG: HistorialPuesto anterior cerrado para empleado: " + savedEmpleado.getNombres() + " - Fecha Fin: " + currentHistorial.getFechaFin());
            } else {
                System.out.println("ADVERTENCIA: No se encontró un HistorialPuesto activo para el empleado " + savedEmpleado.getNombres() + " al cambiar de puesto. Creando nuevo historial sin cerrar el anterior.");
            }

            // Crear un nuevo registro en historial_puesto para el nuevo puesto
            HistorialPuesto nuevoHistorial = new HistorialPuesto();
            nuevoHistorial.setEmpleado(savedEmpleado);
            nuevoHistorial.setPuesto(savedEmpleado.getPuesto());
            nuevoHistorial.setFechaInicio(LocalDate.now()); // La fecha de inicio del nuevo puesto es hoy
            nuevoHistorial.setFechaFin(null); // Puesto actual
            historialPuestoRepository.save(nuevoHistorial);
            System.out.println("DEBUG: Nuevo HistorialPuesto creado para empleado: " + savedEmpleado.getNombres() + " - Puesto: " + savedEmpleado.getPuesto().getNombrePuesto() + " - Fecha Inicio: " + nuevoHistorial.getFechaInicio());
        }
        
        return savedEmpleado;
    }

    @Override
    @Transactional
    public void deleteEmpleado(Integer idEmpleado) {
        // Asegúrate de que las relaciones en Empleado (OneToMany con HistorialPuesto, etc.)
        // tienen CascadeType.ALL o que manejas la eliminación de dependencias aquí.
        // Por ejemplo, eliminar todos los historiales de puesto asociados:
        historialPuestoRepository.deleteByEmpleado_IdEmpleado(idEmpleado); // Asumiendo que existe este método en HistorialPuestoRepository
        empleadoRepository.deleteById(idEmpleado);
    }

    @Override
    public Optional<Empleado> getEmpleadoByNumeroDocumento(String numeroDocumento) {
        return empleadoRepository.findByNumeroDocumento(numeroDocumento);
    }

    @Override
    public List<Empleado> getActiveEmpleados() {
        return empleadoRepository.findByEstado(true);
    }

    @Override
    public List<Empleado> getEmpleadosWithCalifiedChildren() {
        return empleadoRepository.findByTieneHijosCalificados(true);
    }

    @Override
    public List<Empleado> getEmpleadosByFechaIngresoBetween(LocalDate startDate, LocalDate endDate) {
        return empleadoRepository.findByFechaIngresoBetween(startDate, endDate);
    }

    @Override
    public List<Empleado> searchEmpleados(String query) {
        // La lógica de pasar null si el query es vacío ya está en la consulta JPQL
        return empleadoRepository.searchByDniOrNameOrLastName(query);
    }
}
