// src/main/java/com/textilima/textilima/service/impl/PlanillaServiceImpl.java
package com.textilima.textilima.service.impl;

// Importaciones de interfaces de repositorio
import com.textilima.textilima.repository.PlanillaRepository;
import com.textilima.textilima.repository.DetallePlanillaRepository;
import com.textilima.textilima.repository.MovimientoPlanillaRepository;
import com.textilima.textilima.repository.AsistenciaRepository;
import com.textilima.textilima.repository.BoletaRepository;

import com.textilima.textilima.model.Asistencia;
import com.textilima.textilima.model.Asistencia.EstadoAsistencia;
import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;
import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.HistorialPuesto;
import com.textilima.textilima.model.MovimientoPlanilla;
import com.textilima.textilima.model.ParametroLegal;
import com.textilima.textilima.model.Planilla;
import com.textilima.textilima.model.Planilla.TipoPlanilla;
import com.textilima.textilima.model.Puesto;
import com.textilima.textilima.model.Boleta;

// Importaciones de interfaces de servicio
import com.textilima.textilima.service.AsistenciaService;
import com.textilima.textilima.service.ConceptoPagoService;
import com.textilima.textilima.service.DetallePlanillaService;
import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.service.HistorialPuestoService;
import com.textilima.textilima.service.MovimientoPlanillaService;
import com.textilima.textilima.service.ParametroLegalService;
import com.textilima.textilima.service.PlanillaService;

import org.springframework.transaction.annotation.Transactional;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlanillaServiceImpl implements PlanillaService {

    @Autowired private PlanillaRepository planillaRepository;
    @Autowired private DetallePlanillaRepository detallePlanillaRepository;
    @Autowired private MovimientoPlanillaRepository movimientoPlanillaRepository;
    @Autowired private AsistenciaRepository asistenciaRepository;
    @Autowired private BoletaRepository boletaRepository;

    @Autowired private EmpleadoService empleadoService;
    @Autowired private ParametroLegalService parametroLegalService;
    @Autowired private ConceptoPagoService conceptoPagoService;
    @Autowired private AsistenciaService asistenciaService;
    @Autowired private DetallePlanillaService detallePlanillaService;
    @Autowired private MovimientoPlanillaService movimientoPlanillaService;
    @Autowired private HistorialPuestoService historialPuestoService;

    @Override
    public List<Planilla> getAllPlanillas() {
        return planillaRepository.findAll();
    }

    @Override
    public Optional<Planilla> getPlanillaById(Integer idPlanilla) {
        return planillaRepository.findById(idPlanilla);
    }

    @Override
    public Planilla savePlanilla(Planilla planilla) {
        return planillaRepository.save(planilla);
    }

    @Override
    @Transactional
    public void eliminarPlanillaCompleta(Integer idPlanilla) {
        Optional<Planilla> planillaOptional = planillaRepository.findById(idPlanilla);
        if (planillaOptional.isPresent()) {
            Planilla planillaToDelete = planillaOptional.get();

            List<DetallePlanilla> detalles = detallePlanillaRepository.findByPlanilla(planillaToDelete);
            for (DetallePlanilla detalle : detalles) {
                Optional<Boleta> boletaExistente = boletaRepository.findByDetallePlanilla(detalle);
                boletaExistente.ifPresent(boleta -> {
                    boletaRepository.delete(boleta);
                    System.out.println("DEBUG: Boleta con ID " + boleta.getIdBoleta() + " eliminada.");
                });
                movimientoPlanillaRepository.deleteAll(detalle.getMovimientosPlanilla());
                System.out.println("DEBUG: Movimientos de planilla para DetallePlanilla ID " + detalle.getIdDetalle() + " eliminados.");
            }

            detallePlanillaRepository.deleteAll(detalles);
            System.out.println("DEBUG: Detalles de planilla para Planilla ID " + idPlanilla + " eliminados.");

            planillaRepository.delete(planillaToDelete);
            System.out.println("DEBUG: Planilla con ID " + idPlanilla + " eliminada.");
        } else {
            throw new RuntimeException("Planilla no encontrada para eliminar con ID: " + idPlanilla);
        }
    }

    @Override
    public Optional<Planilla> getPlanillaByMesAnioAndTipo(Integer mes, Integer anio, TipoPlanilla tipoPlanilla) {
        return planillaRepository.findByMesAndAnioAndTipoPlanilla(mes, anio, tipoPlanilla);
    }

    @Override
    public List<Planilla> getPlanillasByFechaGeneradaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return planillaRepository.findByFechaGeneradaBetween(fechaInicio, fechaFin);
    }

    @Override
    public List<Planilla> getPlanillasByAnioAndTipo(Integer anio, TipoPlanilla tipoPlanilla) {
        return planillaRepository.findByAnioAndTipoPlanilla(anio, tipoPlanilla);
    }

    @Override
    @Transactional
    public Planilla generatePlanilla(Integer mes, Integer anio, TipoPlanilla tipoPlanilla) {
        if (getPlanillaByMesAnioAndTipo(mes, anio, tipoPlanilla).isPresent()) {
            throw new IllegalStateException("Ya existe una planilla de tipo " + tipoPlanilla +
                                            " para el mes " + mes + " y año " + anio);
        }

        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaInicioPeriodo = LocalDate.of(anio, mes, 1);
        LocalDate fechaFinPeriodo = fechaInicioPeriodo.withDayOfMonth(fechaInicioPeriodo.lengthOfMonth());

        ParametroLegal rmv = parametroLegalService.getParametroLegalByCodigoAndFechaVigencia("RMV", fechaFinPeriodo)
                                                   .orElseThrow(() -> new RuntimeException("RMV no encontrado para la fecha: " + fechaFinPeriodo));
        ParametroLegal uit = parametroLegalService.getParametroLegalByCodigoAndFechaVigencia("UIT", fechaFinPeriodo)
                                                   .orElseThrow(() -> new RuntimeException("UIT no encontrado para la fecha: " + fechaFinPeriodo));

        Planilla nuevaPlanilla = new Planilla();
        nuevaPlanilla.setMes(mes);
        nuevaPlanilla.setAnio(anio);
        nuevaPlanilla.setTipoPlanilla(tipoPlanilla);
        nuevaPlanilla.setFechaGenerada(fechaActual);
        nuevaPlanilla.setParamRmv(rmv);
        nuevaPlanilla = planillaRepository.save(nuevaPlanilla);

        List<ConceptoPago> conceptosIngresos = conceptoPagoService.getConceptosPagoByTipo(TipoConcepto.INGRESO);
        List<ConceptoPago> conceptosDescuentos = conceptoPagoService.getConceptosPagoByTipo(TipoConcepto.DESCUENTO);
        List<ConceptoPago> conceptosAportesEmpleador = conceptoPagoService.getConceptosPagoByTipo(TipoConcepto.APORTE_EMPLEADOR);

        List<Empleado> empleadosActivos = empleadoService.getActiveEmpleados();

        if (empleadosActivos.isEmpty()) {
            throw new RuntimeException("No hay empleados activos para generar la planilla.");
        }

        for (Empleado empleado : empleadosActivos) {
            DetallePlanilla detalle = new DetallePlanilla();
            detalle.setPlanilla(nuevaPlanilla);
            detalle.setEmpleado(empleado);

            Optional<HistorialPuesto> puestoHistoricoOpt = historialPuestoService.getPuestoByEmpleadoAndDate(empleado, fechaFinPeriodo);
            Puesto puestoActual = puestoHistoricoOpt.map(HistorialPuesto::getPuesto)
                                                 .orElseThrow(() -> new RuntimeException("No se encontró puesto para el empleado " + empleado.getNombres() + " " + empleado.getApellidos() + " para el periodo de planilla."));

            BigDecimal sueldoBase = puestoActual.getSalarioBase();
            detalle.setSueldoBase(sueldoBase);

            BigDecimal asignacionFamiliar = BigDecimal.ZERO;
            if (empleado.getTieneHijosCalificados() != null && empleado.getTieneHijosCalificados()) {
                asignacionFamiliar = rmv.getValor().multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
            }
            detalle.setAsignacionFamiliar(asignacionFamiliar);

            BigDecimal totalIngresosAdicionales = BigDecimal.ZERO;
            BigDecimal totalDescuentos = BigDecimal.ZERO;
            BigDecimal totalAportesEmpleador = BigDecimal.ZERO;
            List<MovimientoPlanilla> movimientos = new ArrayList<>();

            // --- CÁLCULO Y PERSISTENCIA DE ASISTENCIAS (TARDANZAS, HORAS EXTRAS, AUSENCIAS) ---
            List<Asistencia> asistenciasDelMes = asistenciaService.getAsistenciasByEmpleadoAndFechaBetween(empleado, fechaInicioPeriodo, fechaFinPeriodo);

            int totalMinutosTardanzaMes = 0;
            BigDecimal totalHorasExtras25Mes = BigDecimal.ZERO;
            BigDecimal totalHorasExtras35Mes = BigDecimal.ZERO;
            int diasAusentesMes = 0;

            LocalTime horaInicioJornada = puestoActual.getHoraInicioJornada();
            LocalTime horaFinJornada = puestoActual.getHoraFinJornada();
            int jornadaHoras = puestoActual.getJornadaLaboralHoras();

            BigDecimal diasBaseCalculo = BigDecimal.valueOf(30);
            BigDecimal valorDia = sueldoBase.divide(diasBaseCalculo, 4, RoundingMode.HALF_UP);
            BigDecimal valorHora = sueldoBase.divide(diasBaseCalculo.multiply(BigDecimal.valueOf(jornadaHoras)), 4, RoundingMode.HALF_UP);
            BigDecimal valorMinuto = valorHora.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

            for (Asistencia asistencia : asistenciasDelMes) {
                // Reiniciar los valores para cada asistencia
                asistencia.setMinutosTardanza(0);
                asistencia.setHorasExtras25(BigDecimal.ZERO);
                asistencia.setHorasExtras35(BigDecimal.ZERO);

                if (asistencia.getEstado() == EstadoAsistencia.PRESENTE || asistencia.getEstado() == EstadoAsistencia.TARDANZA) {
                    // Cálculo de Tardanzas por día
                    if (asistencia.getHoraEntrada() != null && horaInicioJornada != null && asistencia.getHoraEntrada().isAfter(horaInicioJornada)) {
                        Duration tardanzaDuration = Duration.between(horaInicioJornada, asistencia.getHoraEntrada());
                        int minutosTardanzaCalculados = (int) tardanzaDuration.toMinutes();
                        asistencia.setMinutosTardanza(minutosTardanzaCalculados);
                        totalMinutosTardanzaMes += minutosTardanzaCalculados;
                    }
                    
                    // Cálculo de Horas Extras por día
                    if (asistencia.getHoraSalida() != null && horaFinJornada != null && asistencia.getHoraSalida().isAfter(horaFinJornada)) {
                        Duration extraDuration = Duration.between(horaFinJornada, asistencia.getHoraSalida());
                        BigDecimal horasExtrasDecimal = BigDecimal.valueOf(extraDuration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

                        BigDecimal horas25Calculadas = BigDecimal.ZERO;
                        BigDecimal horas35Calculadas = BigDecimal.ZERO;

                        if (horasExtrasDecimal.compareTo(BigDecimal.valueOf(2)) <= 0) {
                            horas25Calculadas = horasExtrasDecimal;
                        } else {
                            horas25Calculadas = BigDecimal.valueOf(2);
                            horas35Calculadas = horasExtrasDecimal.subtract(BigDecimal.valueOf(2));
                        }
                        asistencia.setHorasExtras25(horas25Calculadas);
                        asistencia.setHorasExtras35(horas35Calculadas);
                        totalHorasExtras25Mes = totalHorasExtras25Mes.add(horas25Calculadas);
                        totalHorasExtras35Mes = totalHorasExtras35Mes.add(horas35Calculadas);
                    }
                } else if (asistencia.getEstado() == EstadoAsistencia.AUSENTE) {
                    diasAusentesMes++;
                    // Los campos ya se reiniciaron a 0/BigDecimal.ZERO al inicio del bucle,
                    // así que no es necesario volver a establecerlos aquí.
                }
                // Persistir los cambios en cada asistencia después de calcular sus valores
                asistenciaRepository.save(asistencia);
            }

            // Remuneración computable afecta (inicial)
            BigDecimal remuneracionComputableAfecta = sueldoBase.add(asignacionFamiliar);

            // ***** Generar Movimientos de Planilla (Ingresos) *****
            // (Se asume que los conceptos de Horas Extras son remunerativos y afectan la base imponible)
            if (totalHorasExtras25Mes.compareTo(BigDecimal.ZERO) > 0) {
                ConceptoPago horasExtras25Concepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Horas Extras 25%", TipoConcepto.INGRESO)
                                                                        .orElse(null);
                if (horasExtras25Concepto != null) {
                    BigDecimal montoHorasExtras25 = totalHorasExtras25Mes
                        .multiply(valorHora)
                        .multiply(horasExtras25Concepto.getValorReferencial())
                        .setScale(2, RoundingMode.HALF_UP);
                    totalIngresosAdicionales = totalIngresosAdicionales.add(montoHorasExtras25);
                    remuneracionComputableAfecta = remuneracionComputableAfecta.add(montoHorasExtras25); // Afecta la base
                    movimientos.add(new MovimientoPlanilla(null, detalle, horasExtras25Concepto, montoHorasExtras25));
                }
            }
            if (totalHorasExtras35Mes.compareTo(BigDecimal.ZERO) > 0) {
                ConceptoPago horasExtras35Concepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Horas Extras 35%", TipoConcepto.INGRESO)
                                                                        .orElse(null);
                if (horasExtras35Concepto != null) {
                    BigDecimal montoHorasExtras35 = totalHorasExtras35Mes
                        .multiply(valorHora)
                        .multiply(horasExtras35Concepto.getValorReferencial())
                        .setScale(2, RoundingMode.HALF_UP);
                    totalIngresosAdicionales = totalIngresosAdicionales.add(montoHorasExtras35);
                    remuneracionComputableAfecta = remuneracionComputableAfecta.add(montoHorasExtras35); // Afecta la base
                    movimientos.add(new MovimientoPlanilla(null, detalle, horasExtras35Concepto, montoHorasExtras35));
                }
            }
            // Otros ingresos fijos o variables (bonos, etc.)
            for (ConceptoPago concepto : conceptosIngresos) {
                if (!concepto.getNombreConcepto().equalsIgnoreCase("Horas Extras 25%") &&
                    !concepto.getNombreConcepto().equalsIgnoreCase("Horas Extras 35%") &&
                    !concepto.getNombreConcepto().equalsIgnoreCase("Sueldo Base") && // Ya manejado
                    !concepto.getNombreConcepto().equalsIgnoreCase("Asignación Familiar")) { // Ya manejado
                    
                    BigDecimal montoMovimiento = BigDecimal.ZERO;
                    if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.MONTO_FIJO) {
                        montoMovimiento = concepto.getValorReferencial() != null ? concepto.getValorReferencial().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                        montoMovimiento = sueldoBase.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP);
                    }
                    // Aquí se pueden añadir más lógicas para otros tipos de ingresos especiales
                    
                    if (montoMovimiento.compareTo(BigDecimal.ZERO) > 0) {
                        totalIngresosAdicionales = totalIngresosAdicionales.add(montoMovimiento);
                        if (concepto.getEsRemunerativo()) {
                            remuneracionComputableAfecta = remuneracionComputableAfecta.add(montoMovimiento);
                        }
                        movimientos.add(new MovimientoPlanilla(null, detalle, concepto, montoMovimiento));
                    }
                }
            }
            detalle.setTotalIngresosAdicionales(totalIngresosAdicionales);
            detalle.setSueldoBruto(sueldoBase.add(asignacionFamiliar).add(totalIngresosAdicionales));
            detalle.setRemuneracionComputableAfecta(remuneracionComputableAfecta);


            // ***** Generar Movimientos de Planilla (Descuentos) *****
            // Descuento por tardanza
            if (totalMinutosTardanzaMes > 0) {
                ConceptoPago descuentoTardanzaConcepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Descuento por Tardanza", TipoConcepto.DESCUENTO)
                                                                            .orElse(null);
                if (descuentoTardanzaConcepto != null) {
                    BigDecimal montoDescuentoTardanza = BigDecimal.valueOf(totalMinutosTardanzaMes).multiply(valorMinuto).setScale(2, RoundingMode.HALF_UP);
                    totalDescuentos = totalDescuentos.add(montoDescuentoTardanza);
                    movimientos.add(new MovimientoPlanilla(null, detalle, descuentoTardanzaConcepto, montoDescuentoTardanza));
                }
            }
            // Descuento por días de ausencia
            if (diasAusentesMes > 0) {
                ConceptoPago descuentoAusenciaConcepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Descuento por Ausencia", TipoConcepto.DESCUENTO)
                                                                            .orElse(null);
                if (descuentoAusenciaConcepto != null) {
                    BigDecimal montoDescuentoAusencia = valorDia.multiply(BigDecimal.valueOf(diasAusentesMes)).setScale(2, RoundingMode.HALF_UP);
                    totalDescuentos = totalDescuentos.add(montoDescuentoAusencia);
                    movimientos.add(new MovimientoPlanilla(null, detalle, descuentoAusenciaConcepto, montoDescuentoAusencia));
                } else {
                    // Si no hay concepto específico para ausencia, el descuento se aplica directamente
                    BigDecimal montoDescuentoAusencia = valorDia.multiply(BigDecimal.valueOf(diasAusentesMes)).setScale(2, RoundingMode.HALF_UP);
                    totalDescuentos = totalDescuentos.add(montoDescuentoAusencia);
                }
            }

            // Descuentos de pensiones (ONP/AFP)
            if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.ONP) {
                ConceptoPago onpConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Aporte ONP", TipoConcepto.DESCUENTO).orElse(null);
                if (onpConcept != null) {
                    BigDecimal onpMonto = remuneracionComputableAfecta.multiply(onpConcept.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                    totalDescuentos = totalDescuentos.add(onpMonto);
                    movimientos.add(new MovimientoPlanilla(null, detalle, onpConcept, onpMonto));
                }
            } else if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.AFP) {
                ConceptoPago afpFondoConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Aporte AFP Fondo", TipoConcepto.DESCUENTO).orElse(null);
                if (afpFondoConcept != null) {
                    BigDecimal afpFondoMonto = remuneracionComputableAfecta.multiply(afpFondoConcept.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                    totalDescuentos = totalDescuentos.add(afpFondoMonto);
                    movimientos.add(new MovimientoPlanilla(null, detalle, afpFondoConcept, afpFondoMonto));
                }
                ConceptoPago afpComisionConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Comisión AFP", TipoConcepto.DESCUENTO).orElse(null);
                if (afpComisionConcept != null) {
                    BigDecimal afpComisionMonto = remuneracionComputableAfecta.multiply(afpComisionConcept.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                    totalDescuentos = totalDescuentos.add(afpComisionMonto);
                    movimientos.add(new MovimientoPlanilla(null, detalle, afpComisionConcept, afpComisionMonto));
                }
                ConceptoPago afpPrimaConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Prima Seguro AFP", TipoConcepto.DESCUENTO).orElse(null);
                if (afpPrimaConcept != null) {
                    BigDecimal afpPrimaMonto = remuneracionComputableAfecta.multiply(afpPrimaConcept.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                    totalDescuentos = totalDescuentos.add(afpPrimaMonto);
                    movimientos.add(new MovimientoPlanilla(null, detalle, afpPrimaConcept, afpPrimaMonto));
                }
            }
            // Otros descuentos (préstamos, adelantos, etc.)
            for (ConceptoPago concepto : conceptosDescuentos) {
                if (!concepto.getNombreConcepto().equalsIgnoreCase("Descuento por Tardanza") &&
                    !concepto.getNombreConcepto().equalsIgnoreCase("Descuento por Ausencia") &&
                    !concepto.getNombreConcepto().equalsIgnoreCase("Aporte ONP") &&
                    !concepto.getNombreConcepto().equalsIgnoreCase("Aporte AFP Fondo") &&
                    !concepto.getNombreConcepto().equalsIgnoreCase("Comisión AFP") &&
                    !concepto.getNombreConcepto().equalsIgnoreCase("Prima Seguro AFP")) {
                    
                    BigDecimal montoDescuento = BigDecimal.ZERO;
                    if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.MONTO_FIJO) {
                        montoDescuento = concepto.getValorReferencial() != null ? concepto.getValorReferencial().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                        montoDescuento = remuneracionComputableAfecta.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP);
                    }
                    // Lógica para otros descuentos especiales
                    
                    if (montoDescuento.compareTo(BigDecimal.ZERO) > 0) {
                        totalDescuentos = totalDescuentos.add(montoDescuento);
                        movimientos.add(new MovimientoPlanilla(null, detalle, concepto, montoDescuento));
                    }
                }
            }
            detalle.setTotalDescuentos(totalDescuentos);
            detalle.setSueldoNeto(detalle.getSueldoBruto().subtract(totalDescuentos));

            // ***** Generar Movimientos de Planilla (Aportes del Empleador) *****
            ConceptoPago essaludConcepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Essalud", TipoConcepto.APORTE_EMPLEADOR).orElse(null);
            if (essaludConcepto != null) {
                BigDecimal essaludMonto = remuneracionComputableAfecta.multiply(essaludConcepto.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                totalAportesEmpleador = totalAportesEmpleador.add(essaludMonto);
                movimientos.add(new MovimientoPlanilla(null, detalle, essaludConcepto, essaludMonto));
            }
            // Otros aportes del empleador (SCTR, Senati, etc.)
            for (ConceptoPago concepto : conceptosAportesEmpleador) {
                if (!concepto.getNombreConcepto().equalsIgnoreCase("Essalud")) {
                    BigDecimal montoAporte = BigDecimal.ZERO;
                    if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                        montoAporte = remuneracionComputableAfecta.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP);
                    }
                    if (montoAporte.compareTo(BigDecimal.ZERO) > 0) {
                        totalAportesEmpleador = totalAportesEmpleador.add(montoAporte);
                        movimientos.add(new MovimientoPlanilla(null, detalle, concepto, montoAporte));
                    }
                }
            }
            detalle.setTotalAportesEmpleador(totalAportesEmpleador);

            detalle.setMovimientosPlanilla(movimientos);
            detallePlanillaService.saveDetallePlanilla(detalle);
        }

        return nuevaPlanilla;
    }

    @Override
    @Transactional(readOnly = true)
    public Planilla getPlanillaWithDetails(Integer idPlanilla) {
        Planilla planilla = planillaRepository.findById(idPlanilla)
                .orElseThrow(() -> new RuntimeException("Planilla no encontrada con ID: " + idPlanilla));
        
        List<DetallePlanilla> detallesPlanilla = detallePlanillaRepository.findByPlanilla(planilla);
        detallesPlanilla.forEach(dp -> {
            dp.setMovimientosPlanilla(movimientoPlanillaRepository.findByDetallePlanilla(dp));
        });
        planilla.setDetallesPlanilla(detallesPlanilla); 
        return planilla;
    }
}
