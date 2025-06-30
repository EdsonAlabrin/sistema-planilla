package com.textilima.textilima.service.impl;

import com.textilima.textilima.model.Asistencia;
import com.textilima.textilima.model.Asistencia.EstadoAsistencia;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Puesto;
import com.textilima.textilima.repository.AsistenciaRepository;
import com.textilima.textilima.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Override
    public List<Asistencia> getAllAsistencias() {
        return asistenciaRepository.findAll();
    }

    @Override
    public Optional<Asistencia> getAsistenciaById(Integer id) {
        return asistenciaRepository.findById(id);
    }

    @Override
    public Asistencia saveAsistencia(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    @Override
    public void deleteAsistencia(Integer id) {
        asistenciaRepository.deleteById(id);
    }

    @Override
    public Optional<Asistencia> getAsistenciaByEmpleadoAndFecha(Empleado empleado, LocalDate fecha) {
        return asistenciaRepository.findByEmpleadoAndFecha(empleado, fecha);
    }

    @Override
    public List<Asistencia> getAsistenciasByEmpleadoAndFechaBetween(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin) {
        return asistenciaRepository.findByEmpleadoAndFechaBetween(empleado, fechaInicio, fechaFin);
    }

    @Override
    public List<Asistencia> getAsistenciasByEmpleadoAndFechaBetweenAndEstados(Empleado empleado, LocalDate fechaInicio, LocalDate fechaFin, List<Asistencia.EstadoAsistencia> estados) {
        return asistenciaRepository.findByEmpleadoAndFechaBetweenAndEstadoIn(empleado, fechaInicio, fechaFin, estados);
    }

    @Override
    public Asistencia calculateTardinessAndOvertime(Asistencia asistencia, Puesto puesto) {
        if (asistencia.getHoraEntrada() == null || asistencia.getHoraSalida() == null ||
            puesto.getHoraInicioJornada() == null || puesto.getHoraFinJornada() == null ||
            puesto.getJornadaLaboralHoras() == null) {
            throw new IllegalArgumentException("Información de asistencia o jornada laboral incompleta para calcular tardanzas/horas extras.");
        }

        LocalTime horaEntradaReal = asistencia.getHoraEntrada();
        LocalTime horaSalidaReal = asistencia.getHoraSalida();
        LocalTime horaInicioJornada = puesto.getHoraInicioJornada();
        LocalTime horaFinJornada = puesto.getHoraFinJornada();
        Integer jornadaLaboralHoras = puesto.getJornadaLaboralHoras();

        asistencia.setMinutosTardanza(0);
        asistencia.setHorasExtras25(BigDecimal.ZERO);
        asistencia.setHorasExtras35(BigDecimal.ZERO);
        asistencia.setEstado(Asistencia.EstadoAsistencia.PRESENTE);

        // 1. Calcular Tardanza
        if (horaEntradaReal.isAfter(horaInicioJornada)) {
            Duration tardanzaDuration = Duration.between(horaInicioJornada, horaEntradaReal);
            long minutosTardanza = tardanzaDuration.toMinutes();
            asistencia.setMinutosTardanza((int) minutosTardanza);
            asistencia.setEstado(Asistencia.EstadoAsistencia.TARDANZA);
        }

        // 2. Calcular Horas Efectivas Trabajadas y Horas Extras
        Duration duracionTrabajada = Duration.between(horaEntradaReal, horaSalidaReal);
        long minutosTrabajados = duracionTrabajada.toMinutes();

        long jornadaLaboralMinutos = jornadaLaboralHoras * 60L;

        if (minutosTrabajados > jornadaLaboralMinutos) {
            // Horas adicionales más allá del fin de jornada o más allá de las horas de jornada
            Duration horasExtrasRawDuration = Duration.between(horaFinJornada, horaSalidaReal);

            if (horasExtrasRawDuration.isNegative()) {
                horasExtrasRawDuration = Duration.ZERO;
            }

            BigDecimal totalHorasExtras = BigDecimal.valueOf(horasExtrasRawDuration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

            BigDecimal limiteHoras25 = BigDecimal.valueOf(2.0);
            BigDecimal horas25 = BigDecimal.ZERO;
            BigDecimal horas35 = BigDecimal.ZERO;

            if (totalHorasExtras.compareTo(BigDecimal.ZERO) > 0) {
                if (totalHorasExtras.compareTo(limiteHoras25) <= 0) {
                    horas25 = totalHorasExtras;
                } else {
                    horas25 = limiteHoras25;
                    horas35 = totalHorasExtras.subtract(limiteHoras25);
                }
            }

            asistencia.setHorasExtras25(horas25);
            asistencia.setHorasExtras35(horas35);
        }

        return asistencia;
    }
}
