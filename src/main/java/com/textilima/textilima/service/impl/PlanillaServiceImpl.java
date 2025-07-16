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
import com.textilima.textilima.model.enums.RegimenLaboral;
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
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
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

        ParametroLegal rmv = parametroLegalService.getParametroLegalByCodigoAndFechaVigencia("RMV_PERU", fechaFinPeriodo)
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
            LocalDate fechaIngreso = empleado.getFechaIngreso(); 
            if (fechaIngreso == null) {
                throw new RuntimeException("Fecha de contratación no definida para el empleado: " + empleado.getNombres());
            }

            if (empleado.getFechaIngreso().isAfter(fechaFinPeriodo) ||
                (empleado.getFechaCese() != null && empleado.getFechaCese().isBefore(fechaInicioPeriodo))) {
                System.out.println("Empleado " + empleado.getNombres() + " no activo en el periodo " + mes + "/" + anio + ". Omitiendo.");
                continue;
            }
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

            switch (tipoPlanilla) {
                case MENSUAL:
                // Remuneración computable afecta (inicial) para planilla mensual
                    BigDecimal remuneracionComputableAfectaMensual = sueldoBase.add(asignacionFamiliar);
                    
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
                    
                    final BigDecimal MONTO_FIJO_TARDANZA = new BigDecimal("10.00"); 

                    for (Asistencia asistencia : asistenciasDelMes) {
                        asistencia.setMinutosTardanza(0);
                        asistencia.setHorasExtras25(BigDecimal.ZERO);
                        asistencia.setHorasExtras35(BigDecimal.ZERO);

                        if (asistencia.getEstado() == EstadoAsistencia.PRESENTE || asistencia.getEstado() == EstadoAsistencia.TARDANZA) {
                            if (asistencia.getHoraEntrada() != null && horaInicioJornada != null && asistencia.getHoraEntrada().isAfter(horaInicioJornada)) {
                                Duration tardanzaDuration = Duration.between(horaInicioJornada, asistencia.getHoraEntrada());
                                int minutosTardanzaCalculados = (int) tardanzaDuration.toMinutes();
                                asistencia.setMinutosTardanza(minutosTardanzaCalculados);
                                totalMinutosTardanzaMes += minutosTardanzaCalculados;
                            }
                            
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
                        }
                        asistenciaRepository.save(asistencia);
                    }

                    System.out.println("\n--- DEBUG: Asistencia Consolidada para " + empleado.getNombres() + " " + empleado.getApellidos() + " ---");
                    System.out.println("  Sueldo Base: " + sueldoBase);
                    System.out.println("  Valor Día (calculado): " + valorDia);
                    System.out.println("  Valor Hora (calculado): " + valorHora);
                    System.out.println("  Valor Minuto (calculado): " + valorMinuto);
                    System.out.println("  Minutos de Tardanza TOTAL del mes: " + totalMinutosTardanzaMes);
                    System.out.println("  Días Ausentes TOTAL del mes: " + diasAusentesMes);
                    System.out.println("  Horas Extras 25% TOTAL del mes: " + totalHorasExtras25Mes);
                    System.out.println("  Horas Extras 35% TOTAL del mes: " + totalHorasExtras35Mes);
                    System.out.println("------------------------------------------------------------------");

                    // ***** Generar Movimientos de Planilla (Ingresos - Mensual) *****
                    if (totalHorasExtras25Mes.compareTo(BigDecimal.ZERO) > 0) {
                        ConceptoPago horasExtras25Concepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Horas Extras 25%", TipoConcepto.INGRESO)
                                                                                .orElse(null);
                        if (horasExtras25Concepto != null) {
                            BigDecimal montoHorasExtras25 = totalHorasExtras25Mes
                                .multiply(valorHora)
                                .multiply(BigDecimal.valueOf(1.25)) // Valor referencial es el % de incremento (1.25)
                                .setScale(2, RoundingMode.HALF_UP);
                            totalIngresosAdicionales = totalIngresosAdicionales.add(montoHorasExtras25);
                            remuneracionComputableAfectaMensual = remuneracionComputableAfectaMensual.add(montoHorasExtras25); 
                            movimientos.add(new MovimientoPlanilla(null, detalle, horasExtras25Concepto, montoHorasExtras25));
                            System.out.println("  DEBUG: Se añadió Horas Extras 25%: " + montoHorasExtras25);
                        }
                    }
                    if (totalHorasExtras35Mes.compareTo(BigDecimal.ZERO) > 0) {
                        ConceptoPago horasExtras35Concepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Horas Extras 35%", TipoConcepto.INGRESO)
                                                                                .orElse(null);
                        if (horasExtras35Concepto != null) {
                            BigDecimal montoHorasExtras35 = totalHorasExtras35Mes
                                .multiply(valorHora)
                                .multiply(BigDecimal.valueOf(1.35)) 
                                .setScale(2, RoundingMode.HALF_UP);
                            totalIngresosAdicionales = totalIngresosAdicionales.add(montoHorasExtras35);
                            remuneracionComputableAfectaMensual = remuneracionComputableAfectaMensual.add(montoHorasExtras35); 
                            movimientos.add(new MovimientoPlanilla(null, detalle, horasExtras35Concepto, montoHorasExtras35));
                            System.out.println("  DEBUG: Se añadió Horas Extras 35%: " + montoHorasExtras35);
                        }
                    }
                    // Otros ingresos fijos o variables (bonos, etc.) para planilla mensual
                    for (ConceptoPago concepto : conceptosIngresos) {
                        if (!concepto.getNombreConcepto().equalsIgnoreCase("Horas Extras 25%") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Horas Extras 35%") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Sueldo Base") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Asignación Familiar") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Gratificación Ordinaria") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Bonificación Extraordinaria (Ley)") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Liquidación de Beneficios Sociales") && 
                            !concepto.getNombreConcepto().equalsIgnoreCase("Compensación por Tiempo de Servicios"))
                            {
                            
                            BigDecimal montoMovimiento = BigDecimal.ZERO;
                            if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.MONTO_FIJO) {
                                montoMovimiento = concepto.getValorReferencial() != null ? concepto.getValorReferencial().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                            } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                                montoMovimiento = sueldoBase.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP);
                            }
                            
                            if (montoMovimiento.compareTo(BigDecimal.ZERO) > 0) {
                                totalIngresosAdicionales = totalIngresosAdicionales.add(montoMovimiento);
                                if (concepto.getEsRemunerativo()) {
                                    remuneracionComputableAfectaMensual = remuneracionComputableAfectaMensual.add(montoMovimiento);
                                }
                                movimientos.add(new MovimientoPlanilla(null, detalle, concepto, montoMovimiento));
                            }
                        }
                    }
                    detalle.setTotalIngresosAdicionales(totalIngresosAdicionales);
                    detalle.setSueldoBruto(sueldoBase.add(asignacionFamiliar).add(totalIngresosAdicionales));
                    detalle.setRemuneracionComputableAfecta(remuneracionComputableAfectaMensual);


                    // ***** Generar Movimientos de Planilla (Descuentos - Mensual) *****
                    // Descuento por tardanza
                    if (totalMinutosTardanzaMes > 0) {
                        ConceptoPago descuentoTardanzaConcepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Descuento por Tardanza", TipoConcepto.DESCUENTO)
                                                                                    .orElse(null);
                        System.out.println("DEBUG: Concepto 'Descuento por Tardanza' encontrado: " + (descuentoTardanzaConcepto != null ? descuentoTardanzaConcepto.getNombreConcepto() : "NULL")); // <-- Añade esta línea
                        System.out.println("DEBUG: totalMinutosTardanzaMes: " + totalMinutosTardanzaMes);

                        if (descuentoTardanzaConcepto != null) { // <--- Asegúrate de que esta condición exista y se cumpla
                            BigDecimal montoDescuentoTardanzaPorMinuto = BigDecimal.valueOf(totalMinutosTardanzaMes).multiply(valorMinuto).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal montoTotalDescuentoTardanza = montoDescuentoTardanzaPorMinuto.add(MONTO_FIJO_TARDANZA);
                            System.out.println("DEBUG: Monto calculado para Descuento por Tardanza: " + montoTotalDescuentoTardanza); // <-- Añade esta línea

                            totalDescuentos = totalDescuentos.add(montoTotalDescuentoTardanza);
                            movimientos.add(new MovimientoPlanilla(null, detalle, descuentoTardanzaConcepto, montoTotalDescuentoTardanza.negate()));
                        } else {
                            System.err.println("Advertencia: El Concepto 'Descuento por Tardanza' no fue encontrado por el servicio, a pesar de que la base de datos lo muestra.");
                        }
                    }
                    // Descuento por días de ausencia
                    if (diasAusentesMes > 0) {
                        ConceptoPago descuentoAusenciaConcepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Descuento por Ausencia", TipoConcepto.DESCUENTO)
                                                                                    .orElse(null);
                        BigDecimal montoDescuentoAusencia = valorDia.multiply(BigDecimal.valueOf(diasAusentesMes)).setScale(2, RoundingMode.HALF_UP);
                        totalDescuentos = totalDescuentos.add(montoDescuentoAusencia);
                        movimientos.add(new MovimientoPlanilla(null, detalle, descuentoAusenciaConcepto, montoDescuentoAusencia.negate()));
                    }

                    // Descuentos de pensiones (ONP/AFP) - Mantenidos como estaban en tu código
                    if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.ONP) {
                        ConceptoPago onpConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Aporte ONP", TipoConcepto.DESCUENTO).orElse(null);
                        if (onpConcept != null) {
                            BigDecimal tasaOnp = onpConcept.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);
                            BigDecimal onpMonto = remuneracionComputableAfectaMensual.multiply(tasaOnp).setScale(2, RoundingMode.HALF_UP);

                            totalDescuentos = totalDescuentos.add(onpMonto);
                            // --- CAMBIO 2: Aplicar .negate() al monto del MovimientoPlanilla para descuentos ---
                            movimientos.add(new MovimientoPlanilla(null, detalle, onpConcept, onpMonto.negate()));
                        }
                    } else if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.AFP) {
                        ConceptoPago afpFondoConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Aporte AFP Fondo", TipoConcepto.DESCUENTO).orElse(null);
                        if (afpFondoConcept != null) {
                            BigDecimal tasaAfpFondo = afpFondoConcept.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);
                            BigDecimal afpFondoMonto = remuneracionComputableAfectaMensual.multiply(tasaAfpFondo).setScale(2, RoundingMode.HALF_UP);

                            totalDescuentos = totalDescuentos.add(afpFondoMonto);
                            // --- CAMBIO 4: Aplicar .negate() ---
                            movimientos.add(new MovimientoPlanilla(null, detalle, afpFondoConcept, afpFondoMonto.negate()));
                        }
                        ConceptoPago afpComisionConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Comisión AFP", TipoConcepto.DESCUENTO).orElse(null);
                        if (afpComisionConcept != null) {
                            BigDecimal tasaAfpComision = afpComisionConcept.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);
                            BigDecimal afpComisionMonto = remuneracionComputableAfectaMensual.multiply(tasaAfpComision).setScale(2, RoundingMode.HALF_UP);

                            totalDescuentos = totalDescuentos.add(afpComisionMonto);
                            // --- CAMBIO 6: Aplicar .negate() ---
                            movimientos.add(new MovimientoPlanilla(null, detalle, afpComisionConcept, afpComisionMonto.negate()));
                        }
                        ConceptoPago afpPrimaConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Prima Seguro AFP", TipoConcepto.DESCUENTO).orElse(null);
                        if (afpPrimaConcept != null) {
                            BigDecimal tasaAfpPrima = afpPrimaConcept.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);
                            BigDecimal afpPrimaMonto = remuneracionComputableAfectaMensual.multiply(tasaAfpPrima).setScale(2, RoundingMode.HALF_UP);

                            totalDescuentos = totalDescuentos.add(afpPrimaMonto);
                            // --- CAMBIO 8: Aplicar .negate() ---
                            movimientos.add(new MovimientoPlanilla(null, detalle, afpPrimaConcept, afpPrimaMonto.negate()));
                        }
                    }
                    // Otros descuentos (préstamos, adelantos, etc.)
                    for (ConceptoPago concepto : conceptosDescuentos) {
                        if (!concepto.getNombreConcepto().equalsIgnoreCase("Descuento por Tardanza") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Descuento por Ausencia") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Aporte ONP") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Aporte AFP Fondo") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Comisión AFP") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Prima Seguro AFP") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Impuesto a la Renta 5ta Categoría")) {
                            
                            BigDecimal montoDescuento = BigDecimal.ZERO;
                            if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.MONTO_FIJO) {
                                montoDescuento = concepto.getValorReferencial() != null ? concepto.getValorReferencial().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                            } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                                montoDescuento = remuneracionComputableAfectaMensual.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP);
                            }
                            
                            if (montoDescuento.compareTo(BigDecimal.ZERO) > 0) {
                                totalDescuentos = totalDescuentos.add(montoDescuento);
                                movimientos.add(new MovimientoPlanilla(null, detalle, concepto, montoDescuento.negate()));
                            }
                        }
                    }
                    if (empleado.getRegimenLaboral() == RegimenLaboral.REGIMEN_GENERAL) {
                        // Obtener el concepto de Renta de Quinta ya sembrado
                        ConceptoPago rentaQuintaConcept = conceptoPagoService.getConceptoPagoByNombreAndTipo("Impuesto a la Renta 5ta Categoría", TipoConcepto.DESCUENTO)
                                                                              .orElse(null);

                        if (rentaQuintaConcept != null) {

                            BigDecimal sueldoBrutoMensual = sueldoBase.add(asignacionFamiliar).add(totalIngresosAdicionales);
                            BigDecimal valorUIT = uit.getValor(); 
                            
                            BigDecimal proyeccionAnualIngresos = sueldoBrutoMensual.multiply(new BigDecimal("12"));
                            proyeccionAnualIngresos = proyeccionAnualIngresos
                            .add(sueldoBase) // Gratificación Julio
                            .add(sueldoBase) // Gratificación Diciembre
                            .add(sueldoBase.multiply(new BigDecimal("0.09"))) // Bonificación Extraordinaria Julio (9% de gratificación)
                            .add(sueldoBase.multiply(new BigDecimal("0.09")));
                            BigDecimal deduccion7UITS = valorUIT.multiply(new BigDecimal("7"));
                            BigDecimal baseImponibleAnual = proyeccionAnualIngresos.subtract(deduccion7UITS);
                            if (baseImponibleAnual.compareTo(BigDecimal.ZERO) < 0) {
                                baseImponibleAnual = BigDecimal.ZERO;
                            }

                            BigDecimal impuestoAnualCalculado = BigDecimal.ZERO;
                            BigDecimal restanteParaCalculo = baseImponibleAnual;

                            BigDecimal limiteTramo1 = valorUIT.multiply(new BigDecimal("5"));
                        if (restanteParaCalculo.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal aplica = restanteParaCalculo.min(limiteTramo1);
                            impuestoAnualCalculado = impuestoAnualCalculado.add(aplica.multiply(new BigDecimal("0.08")));
                            restanteParaCalculo = restanteParaCalculo.subtract(aplica);
                        }

                        // Tramo 2: Por el exceso de 5 UITs y hasta 20 UITs (14%)
                        // Este tramo es por las siguientes 15 UITs (20 - 5 = 15)
                        BigDecimal limiteTramo2 = valorUIT.multiply(new BigDecimal("15")); // Esto es el *siguiente* tramo, no el acumulado
                        if (restanteParaCalculo.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal aplica = restanteParaCalculo.min(limiteTramo2);
                            impuestoAnualCalculado = impuestoAnualCalculado.add(aplica.multiply(new BigDecimal("0.14")));
                            restanteParaCalculo = restanteParaCalculo.subtract(aplica);
                        }

                        // Tramo 3: Por el exceso de 20 UITs y hasta 35 UITs (17%)
                        // Siguientes 15 UITs (35 - 20 = 15)
                        BigDecimal limiteTramo3 = valorUIT.multiply(new BigDecimal("15"));
                        if (restanteParaCalculo.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal aplica = restanteParaCalculo.min(limiteTramo3);
                            impuestoAnualCalculado = impuestoAnualCalculado.add(aplica.multiply(new BigDecimal("0.17")));
                            restanteParaCalculo = restanteParaCalculo.subtract(aplica);
                        }

                        // Tramo 4: Por el exceso de 35 UITs y hasta 45 UITs (20%)
                        // Siguientes 10 UITs (45 - 35 = 10)
                        BigDecimal limiteTramo4 = valorUIT.multiply(new BigDecimal("10"));
                        if (restanteParaCalculo.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal aplica = restanteParaCalculo.min(limiteTramo4);
                            impuestoAnualCalculado = impuestoAnualCalculado.add(aplica.multiply(new BigDecimal("0.20")));
                            restanteParaCalculo = restanteParaCalculo.subtract(aplica);
                        }

                        // Tramo 5: Por el exceso de 45 UITs (30%)
                        if (restanteParaCalculo.compareTo(BigDecimal.ZERO) > 0) {
                            impuestoAnualCalculado = impuestoAnualCalculado.add(restanteParaCalculo.multiply(new BigDecimal("0.30")));
                        }
                        BigDecimal impuestoMensualARetener = impuestoAnualCalculado.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
                            if (impuestoMensualARetener.compareTo(BigDecimal.ZERO) > 0) {
                                totalDescuentos = totalDescuentos.add(impuestoMensualARetener);
                                // Asegúrate de que rentaQuintaConcept exista y sea el correcto
                                movimientos.add(new MovimientoPlanilla(null, detalle, rentaQuintaConcept, impuestoMensualARetener.negate()));
                            }
                        } else {
                            System.err.println("Advertencia: Concepto 'Impuesto a la Renta 5ta Categoría' no encontrado. Asegúrese de ejecutar seedConceptosPago().");
                        }
                    }

                    detalle.setTotalDescuentos(totalDescuentos);
                    detalle.setSueldoNeto(detalle.getSueldoBruto().subtract(totalDescuentos));

                    // ***** Generar Movimientos de Planilla (Aportes del Empleador - Mensual) *****
                    ConceptoPago essaludConcepto = conceptoPagoService.getConceptoPagoByNombreAndTipo("Essalud", TipoConcepto.APORTE_EMPLEADOR).orElse(null);
                    if (essaludConcepto != null) {
                        BigDecimal tasaEssalud = essaludConcepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64);
                        BigDecimal essaludMonto = remuneracionComputableAfectaMensual.multiply(tasaEssalud).setScale(2, RoundingMode.HALF_UP);

                        totalAportesEmpleador = totalAportesEmpleador.add(essaludMonto);
                        // Los aportes del empleador no suelen ir en negativo, ya que son gastos de la empresa, no deducciones del empleado.
                        // Se mantienen como positivos.
                        movimientos.add(new MovimientoPlanilla(null, detalle, essaludConcepto, essaludMonto));
                    }
                    // Otros aportes del empleador (SCTR, Senati, etc.)
                    for (ConceptoPago concepto : conceptosAportesEmpleador) {
                        if (!concepto.getNombreConcepto().equalsIgnoreCase("Essalud")) {
                            BigDecimal montoAporte = BigDecimal.ZERO;
                            if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                                montoAporte = remuneracionComputableAfectaMensual.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP);
                            }
                            if (montoAporte.compareTo(BigDecimal.ZERO) > 0) {
                                totalAportesEmpleador = totalAportesEmpleador.add(montoAporte);
                                movimientos.add(new MovimientoPlanilla(null, detalle, concepto, montoAporte));
                            }
                        }
                    }
                    detalle.setTotalAportesEmpleador(totalAportesEmpleador);
                    break; // Fin de la lógica para MENSUAL
                case CTS:
                    if (mes != 5 && mes != 11) {
                        throw new RuntimeException("La planilla de CTS solo puede generarse para los meses de Mayo (5) o Noviembre (11). Mes solicitado: " + mes);
                    }
                    // --- Lógica para CÁLCULO DE CTS ---
                    ConceptoPago conceptoCts = conceptoPagoService.getConceptoPagoByNombreAndTipo("CTS", TipoConcepto.INGRESO)
                                                                 .orElseThrow(() -> new RuntimeException("Concepto 'Compensación por Tiempo de Servicios' no encontrado."));
                    detalle.setTotalAportesEmpleador(BigDecimal.ZERO); 
                    detalle.setTotalIngresosAdicionales(BigDecimal.ZERO); // También inicializa si vas a sumarle después.
                    detalle.setTotalDescuentos(BigDecimal.ZERO);
                    // Remuneración computable para CTS (Sueldo Base + Asignación Familiar + 1/6 Gratificación)
                    BigDecimal remuneracionComputableCts = sueldoBase.add(asignacionFamiliar);

                    BigDecimal sextaParteGratificacion = BigDecimal.ZERO; // Valor temporal, reemplázalo con la lógica real
                    remuneracionComputableCts = remuneracionComputableCts.add(sextaParteGratificacion);
                    detalle.setRemuneracionComputableAfecta(remuneracionComputableCts);

                    LocalDate inicioPeriodoCTS;
                    LocalDate finPeriodoCTS;

                    if (mes == 5) { // Depósito de Mayo: Periodo Noviembre-Abril
                        inicioPeriodoCTS = LocalDate.of(anio - 1, 11, 1);
                        finPeriodoCTS = LocalDate.of(anio, 4, 30);
                    } else { // mes == 11 (Depósito de Noviembre: Periodo Mayo-Octubre)
                        inicioPeriodoCTS = LocalDate.of(anio, 5, 1);
                        finPeriodoCTS = LocalDate.of(anio, 10, 31);
                    }
                    
                    // Ajustar inicio del periodo si la fecha de ingreso es posterior
                    if (fechaIngreso.isAfter(inicioPeriodoCTS)) {
                        inicioPeriodoCTS = fechaIngreso;
                    }

                    int mesesComputables = 0;
                    int diasComputables = 0;
                    // Iterar mes por mes para calcular meses y días computables
                    LocalDate currentDay = inicioPeriodoCTS;
                    while (!currentDay.isAfter(finPeriodoCTS)) {
                        LocalDate endOfMonth = currentDay.with(TemporalAdjusters.lastDayOfMonth());
                        if (endOfMonth.isAfter(finPeriodoCTS)) {
                            endOfMonth = finPeriodoCTS;
                        }

                        // Calcular días en el mes actual dentro del periodo computable
                        long daysWorkedInMonth = Duration.between(currentDay.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay()).toDays();

                        if (daysWorkedInMonth >= 15) {
                            mesesComputables++;
                        } else if (daysWorkedInMonth > 0) {
                            diasComputables += daysWorkedInMonth;
                        }
                        
                        currentDay = endOfMonth.plusDays(1); // Mover al inicio del siguiente mes
                    }
                    mesesComputables += diasComputables / 30;
                    diasComputables = diasComputables % 30;
                    
                    // Cálculo de la CTS
                    BigDecimal montoCtsPorMes = remuneracionComputableCts.divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP);
                    BigDecimal montoCtsPorDia = remuneracionComputableCts.divide(BigDecimal.valueOf(360), 4, RoundingMode.HALF_UP);
                    
                    BigDecimal montoCTS = (montoCtsPorMes.multiply(BigDecimal.valueOf(mesesComputables)))
                                          .add(montoCtsPorDia.multiply(BigDecimal.valueOf(diasComputables)))
                                          .setScale(2, RoundingMode.HALF_UP);

                    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoCts, montoCTS));
                    detalle.setTotalAportesEmpleador(detalle.getTotalAportesEmpleador().add(montoCTS));
                    detalle.setSueldoBruto(montoCTS); // En un reporte CTS, el "bruto" puede ser el monto a depositar
                    detalle.setSueldoNeto(montoCTS); // Y el "neto" también
                    detalle.setTotalIngresosAdicionales(detalle.getTotalIngresosAdicionales().add(montoCTS)); // Puedes usarlo si la boleta de CTS tiene este campo
                    detalle.setTotalDescuentos(BigDecimal.ZERO);
                    break;

                case GRATIFICACION:

                    if (mes != 7 && mes != 12) {
                        throw new RuntimeException("La planilla de Gratificación solo puede generarse para los meses de Julio (7) o Diciembre (12). Mes solicitado: " + mes);
                    }
                    // --- Lógica para CÁLCULO DE GRATIFICACIÓN ---
                    ConceptoPago conceptoGratificacion = conceptoPagoService.getConceptoPagoByNombreAndTipo("Gratificación Ordinaria", TipoConcepto.INGRESO)
                                                                            .orElseThrow(() -> new RuntimeException("Concepto 'Gratificación Ordinaria' no encontrado."));
                    ConceptoPago conceptoBonificacionLey = conceptoPagoService.getConceptoPagoByNombreAndTipo("Bonificación Extraordinaria (Ley)", TipoConcepto.INGRESO)
                                                                                .orElseThrow(() -> new RuntimeException("Concepto 'Bonificación Extraordinaria (Ley)' no encontrado."));

                    
                    // REMUNERACIÓN COMPUTABLE PARA GRATIFICACIÓN: SOLO SUELDO BASE (según tu indicación)
                    BigDecimal remuneracionComputableGratificacion = sueldoBase; 
                    detalle.setRemuneracionComputableAfecta(remuneracionComputableGratificacion);

                    // Variables para el cálculo interno de meses y días computables, NO se persistirán en DetallePlanilla
                    int mesesComputablesGratificacion = 0;
                    int diasComputablesGratificacion = 0; 
                    LocalDate inicioPeriodoGratificacion; // Se calculan para la lógica, no para persistir.
                    LocalDate finPeriodoGratificacion;     // Se calculan para la lógica, no para persistir.
                    
                    if (mes == 7) { // Gratificación de Julio (Periodo Enero-Junio del mismo año)
                        inicioPeriodoGratificacion = LocalDate.of(anio, 1, 1);
                        finPeriodoGratificacion = LocalDate.of(anio, 6, 30);
                    } else if (mes == 12) { // Gratificación de Diciembre (Periodo Julio-Diciembre del mismo año)
                        inicioPeriodoGratificacion = LocalDate.of(anio, 7, 1);
                        finPeriodoGratificacion = LocalDate.of(anio, 12, 31);
                    } else {
                        throw new RuntimeException("Mes de planilla no válido para cálculo de Gratificación: " + mes + ". Debe ser Julio o Diciembre.");
                    }
                    
                    // Ajustar el inicio y fin del periodo basándose en la fecha de ingreso y cese del empleado
                    LocalDate periodoRealInicioGrat = empleado.getFechaIngreso().isAfter(inicioPeriodoGratificacion) ? empleado.getFechaIngreso() : inicioPeriodoGratificacion;
                    LocalDate periodoRealFinGrat = (empleado.getFechaCese() != null && empleado.getFechaCese().isBefore(finPeriodoGratificacion)) ? empleado.getFechaCese() : finPeriodoGratificacion;

                    // Si el periodo real es inválido (ej. fecha de ingreso posterior a fecha de cese ajustada), no hay meses computables
                    if (periodoRealInicioGrat.isAfter(periodoRealFinGrat)) {
                         mesesComputablesGratificacion = 0;
                         diasComputablesGratificacion = 0;
                    } else {
                        // --- INICIO DEL CÁLCULO DE MESES TRABAJADOS (MESES COMPUTABLES) ---
                        LocalDate tempDay = periodoRealInicioGrat;
                        while (!tempDay.isAfter(periodoRealFinGrat)) {
                            LocalDate endOfCurrentMonth = tempDay.with(TemporalAdjusters.lastDayOfMonth());
                            LocalDate effectiveEndOfMonth = endOfCurrentMonth.isAfter(periodoRealFinGrat) ? periodoRealFinGrat : endOfCurrentMonth;

                            long daysInMonthSegment = Duration.between(tempDay.atStartOfDay(), effectiveEndOfMonth.plusDays(1).atStartOfDay()).toDays();

                            if (daysInMonthSegment >= 15) {
                                // Si el segmento del mes tiene 15 días o más, cuenta como un mes completo.
                                mesesComputablesGratificacion++;
                            } else {
                                // Si tiene menos de 15 días, esos días se acumulan.
                                diasComputablesGratificacion += daysInMonthSegment;
                            }
                            
                            tempDay = endOfCurrentMonth.plusDays(1); // Mover al inicio del siguiente mes
                            if (tempDay.isAfter(periodoRealFinGrat)) {
                                break;
                            }
                        }
                        // --- FIN DEL CÁLCULO DE MESES TRABAJADOS ---
                    }

                    // Consolidar días sueltos en meses completos (cada 30 días es un mes para gratificación)
                    // Para gratificación, solo se consideran los meses completos. Los días restantes (diasComputablesGratificacion % 30) NO generan gratificación proporcional.
                    mesesComputablesGratificacion += diasComputablesGratificacion / 30;
                    
                    // Cálculo de la Gratificación
                    BigDecimal montoGratificacion = remuneracionComputableGratificacion
                                                            .multiply(BigDecimal.valueOf(mesesComputablesGratificacion))
                                                            .divide(BigDecimal.valueOf(6), 2, RoundingMode.HALF_UP); 
                    
                    // Bonificación Extraordinaria (Siempre 9% de la gratificación)
                    BigDecimal tasaBonificacionLey = BigDecimal.valueOf(0.09); // Siempre 9%
                    BigDecimal montoBonificacionLey = montoGratificacion.multiply(tasaBonificacionLey).setScale(2, RoundingMode.HALF_UP);

                    // Añadir movimientos a la lista
                    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoGratificacion, montoGratificacion));
                    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoBonificacionLey, montoBonificacionLey));
                    
                    totalIngresosAdicionales = montoGratificacion.add(montoBonificacionLey);

                    // La gratificación NO tendrá descuentos de ONP/AFP ni otros.
                    totalDescuentos = BigDecimal.ZERO; 

                    detalle.setTotalIngresosAdicionales(totalIngresosAdicionales);
                    detalle.setSueldoBruto(montoGratificacion.add(montoBonificacionLey)); 
                    detalle.setTotalDescuentos(totalDescuentos); 
                    detalle.setSueldoNeto(detalle.getSueldoBruto().subtract(totalDescuentos)); 
                    detalle.setTotalAportesEmpleador(BigDecimal.ZERO); // La bonificación del 9% reemplaza el aporte de Essalud
                    
                    break;
                case LIQUIDACION:
                    
                    // Asegúrate de que el empleado tenga una fecha de cese para poder calcular la liquidación.
                    if (empleado.getFechaCese() == null) {
                        throw new RuntimeException("No se puede generar liquidación para empleado sin fecha de cese.");
                    }

                    BigDecimal montoTotalLiquidacion = BigDecimal.ZERO;
                    detalle.setRemuneracionComputableAfecta(BigDecimal.ZERO);
                    ConceptoPago conceptoLiquidacion = conceptoPagoService.getConceptoPagoByNombreAndTipo("Liquidación de Beneficios Sociales", TipoConcepto.INGRESO)
                                                                       .orElseThrow(() -> new RuntimeException("Concepto 'Liquidación de Beneficios Sociales' no encontrado."));

                    // ***** IMPLEMENTACIÓN DE LOS COMPONENTES DE LA LIQUIDACIÓN *****
                    // 1. Sueldo pendiente (si el cese es a mitad de mes)
                    //    BigDecimal sueldoPendiente = calcularSueldoPendiente(empleado, fechaInicioPeriodo, fechaFinPeriodo);
                    //    montoTotalLiquidacion = montoTotalLiquidacion.add(sueldoPendiente);
                    //    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoSueldoPendiente, sueldoPendiente));

                    // 2. Vacaciones truncas
                    //    BigDecimal vacacionesTruncas = calcularVacacionesTruncas(empleado);
                    //    montoTotalLiquidacion = montoTotalLiquidacion.add(vacacionesTruncas);
                    //    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoVacacionesTruncas, vacacionesTruncas));

                    // 3. CTS trunca (si el periodo de cese no coincide con los periodos de pago de CTS)
                    //    BigDecimal ctsTrunca = calcularCtsTrunca(empleado, remuneracionComputableCtsLiquidacion);
                    //    montoTotalLiquidacion = montoTotalLiquidacion.add(ctsTrunca);
                    //    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoCtsTrunca, ctsTrunca));

                    // 4. Gratificación trunca (si el periodo de cese no coincide con los periodos de pago de gratificación)
                    //    BigDecimal gratificacionTrunca = calcularGratificacionTrunca(empleado, remuneracionComputableGratificacionLiquidacion);
                    //    montoTotalLiquidacion = montoTotalLiquidacion.add(gratificacionTrunca);
                    //    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoGratificacionTrunca, gratificacionTrunca));
                    //    Y su bonificación extraordinaria del 9% si aplica

                    // 5. Indemnizaciones (si aplica, por ejemplo, por despido arbitrario)
                    //    BigDecimal indemnizacion = calcularIndemnizacion(empleado, puestoActual, uit);
                    //    montoTotalLiquidacion = montoTotalLiquidacion.add(indemnizacion);
                    //    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoIndemnizacion, indemnizacion));

                    // 6. Otros conceptos (Bonificaciones pendientes, utilidades, etc.)

                    // Ejemplo de un monto placeholder para que el código compile y se pueda probar la estructura
                    montoTotalLiquidacion = sueldoBase.add(asignacionFamiliar); // Esto es solo un ejemplo, no un cálculo real de liquidación
                    movimientos.add(new MovimientoPlanilla(null, detalle, conceptoLiquidacion, montoTotalLiquidacion));
                    totalIngresosAdicionales = totalIngresosAdicionales.add(montoTotalLiquidacion);

                    // Descuentos en liquidación: Préstamos pendientes, adelantos, etc.
                    // No hay descuentos de pensión ONP/AFP ni aportes de empleador en la liquidación de beneficios sociales.
                    // Solo descuentos por saldos pendientes del empleado con la empresa.
                    for (ConceptoPago concepto : conceptosDescuentos) {
                        // Excluir descuentos de pensión y otros que no aplican a liquidaciones
                        if (!concepto.getNombreConcepto().equalsIgnoreCase("Aporte ONP") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Aporte AFP Fondo") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Comisión AFP") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Prima Seguro AFP") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Descuento por Tardanza") &&
                            !concepto.getNombreConcepto().equalsIgnoreCase("Descuento por Ausencia")) {
                            
                            BigDecimal montoDescuento = BigDecimal.ZERO;
                            if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.MONTO_FIJO) {
                                montoDescuento = concepto.getValorReferencial() != null ? concepto.getValorReferencial().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                            } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                                // Aquí se debe decidir la base para el porcentaje en liquidación
                                montoDescuento = sueldoBase.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64)).setScale(2, RoundingMode.HALF_UP);
                            }
                            
                            if (montoDescuento.compareTo(BigDecimal.ZERO) > 0) {
                                totalDescuentos = totalDescuentos.add(montoDescuento);
                                movimientos.add(new MovimientoPlanilla(null, detalle, concepto, montoDescuento));
                            }
                        }
                    }

                    detalle.setTotalIngresosAdicionales(totalIngresosAdicionales);
                    detalle.setSueldoBruto(totalIngresosAdicionales);
                    detalle.setTotalDescuentos(totalDescuentos); 
                    detalle.setSueldoNeto(detalle.getSueldoBruto().subtract(totalDescuentos));
                    detalle.setTotalAportesEmpleador(BigDecimal.ZERO); // No hay aportes del empleador en la boleta de liquidación
                    break;
                default:
                    throw new UnsupportedOperationException("Tipo de planilla no soportado: " + tipoPlanilla);
            }

            // Guardar DetallePlanilla y MovimientoPlanilla
            detalle.setMovimientosPlanilla(movimientos);
            detalle = detallePlanillaRepository.save(detalle); // Guardar el detalle primero para que tenga ID
            for (MovimientoPlanilla mov : movimientos) {
                mov.setDetallePlanilla(detalle); // Asegura que la clave foránea esté establecida
                movimientoPlanillaRepository.save(mov);
            }

            // Crear y guardar Boleta
            Boleta boleta = new Boleta();
            boleta.setDetallePlanilla(detalle);
            boleta.setFechaEmision(fechaActual);
            boleta.setPeriodoMes(detalle.getPlanilla().getMes()); 
            boleta.setPeriodoAnio(detalle.getPlanilla().getAnio());
            boleta.setSueldoBruto(detalle.getSueldoBruto()); 
            boleta.setTotalDescuentos(detalle.getTotalDescuentos());
            boleta.setSueldoNeto(detalle.getSueldoNeto());
            boletaRepository.save(boleta);
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
