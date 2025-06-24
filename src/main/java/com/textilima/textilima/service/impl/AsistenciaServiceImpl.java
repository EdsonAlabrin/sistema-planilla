package com.textilima.textilima.service.impl;

import com.textilima.textilima.entities.Asistencia;
import com.textilima.textilima.entities.Empleado;
import com.textilima.textilima.entities.EstadoAsistencia;
import com.textilima.textilima.repository.AsistenciaRepository;
import com.textilima.textilima.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para manejar transacciones de base de datos

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    /**
     * Repositorio de asistencia para acceder a la base de datos.
     */
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Override
    @Transactional(readOnly = true) // Solo lectura, mejora el rendimiento si no hay escrituras
    public List<Asistencia> obtenerTodasAsistencias() {
        return asistenciaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Asistencia> obtenerAsistenciaPorId(Integer idAsistencia) {
        return asistenciaRepository.findById(idAsistencia);
    }

    @Override
    @Transactional // Las operaciones de guardar modifican la base de datos
    public Asistencia saveAsistencia(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    @Override
    @Transactional
    public void deleteAsistencia(Integer idAsistencia) {
        asistenciaRepository.deleteById(idAsistencia);
    }

    @Override
    @Transactional
    public Asistencia registrarEntrada(Empleado empleado, LocalTime horaEntrada) {
        LocalDate hoy = LocalDate.now();
        // Buscar si ya existe un registro de asistencia para este empleado hoy
        Optional<Asistencia> asistenciaExistente = asistenciaRepository
                .findByEmpleado_IdEmpleadoAndFecha(empleado.getIdEmpleado(), hoy);

        Asistencia asistencia;
        if (asistenciaExistente.isPresent()) {
            // Si ya existe, actualizamos la hora de entrada y el estado a PRESENTE (o
            // TARDANZA si aplica)
            asistencia = asistenciaExistente.get();
            asistencia.setHoraEntrada(horaEntrada);
            // Lógica para determinar si es tardanza (podrías tener una hora_inicio_jornada
            // en un config o puesto)
            // Por ejemplo, si la hora de entrada es después de las 08:00 AM, podría ser
            // tardanza.
            // Para este ejemplo simple, asumiremos que si se registra entrada es PRESENTE,
            // la lógica de tardanza real se implementaría más adelante.
            if (horaEntrada.isAfter(LocalTime.of(8, 0))) { // Ejemplo: si la jornada empieza a las 8 AM
                asistencia.setEstado(EstadoAsistencia.TARDANZA);
            } else {
                asistencia.setEstado(EstadoAsistencia.PRESENTE);
            }
        } else {
            // Si no existe, creamos una nueva asistencia
            asistencia = new Asistencia(empleado, hoy, EstadoAsistencia.PRESENTE);
            asistencia.setHoraEntrada(horaEntrada);
            if (horaEntrada.isAfter(LocalTime.of(8, 0))) {
                asistencia.setEstado(EstadoAsistencia.TARDANZA);
            }
        }
        return asistenciaRepository.save(asistencia);
    }

    @Override
    @Transactional
    public Asistencia registrarSalida(Empleado empleado, LocalTime horaSalida) {
        LocalDate hoy = LocalDate.now();
        // Buscar el registro de asistencia de hoy para este empleado
        Optional<Asistencia> asistenciaExistente = asistenciaRepository
                .findByEmpleado_IdEmpleadoAndFecha(empleado.getIdEmpleado(), hoy);

        if (asistenciaExistente.isPresent()) {
            Asistencia asistencia = asistenciaExistente.get();
            asistencia.setHoraSalida(horaSalida);
            // No cambiamos el estado aquí, se mantiene el de entrada (PRESENTE/TARDANZA) o
            // se podría añadir lógica para AUSENTE si no hay salida
            return asistenciaRepository.save(asistencia);
        }
        // Si no se encontró un registro de entrada para hoy, no se puede registrar la
        // salida
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Asistencia> findAsistenciaByEmpleadoAndFecha(Integer idEmpleado, LocalDate fecha) {
        return asistenciaRepository.findByEmpleado_IdEmpleadoAndFecha(idEmpleado, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asistencia> obtenerAsistenciasByEmpleadoAndDateRange(Integer idEmpleado, LocalDate fechaInicio,
            LocalDate fechaFin) {
        return asistenciaRepository.findByEmpleado_IdEmpleadoAndFechaBetween(idEmpleado, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asistencia> obtenerAsistenciasByFecha(LocalDate fecha) {
        return asistenciaRepository.findByFecha(fecha);
    }
}
