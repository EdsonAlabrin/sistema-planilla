// src/main/java/com/textilima/textilima/service/impl/PlanillaServiceImpl.java
package com.textilima.textilima.service.impl;

// Importaciones de interfaces de repositorio
import com.textilima.textilima.repository.PlanillaRepository;
import com.textilima.textilima.model.Asistencia;
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

// Importaciones de interfaces de servicio
import com.textilima.textilima.service.AsistenciaService;
import com.textilima.textilima.service.ConceptoPagoService;
import com.textilima.textilima.service.DetallePlanillaService;
import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.service.HistorialPuestoService;
import com.textilima.textilima.service.MovimientoPlanillaService;
import com.textilima.textilima.service.ParametroLegalService;
import com.textilima.textilima.service.PlanillaService;

import jakarta.transaction.Transactional;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PlanillaServiceImpl implements PlanillaService {

    @Autowired private PlanillaRepository planillaRepository;
    @Autowired private EmpleadoService empleadoService;
    @Autowired private ParametroLegalService parametroLegalService;
    @Autowired private ConceptoPagoService conceptoPagoService;
    @Autowired private AsistenciaService asistenciaService;
    @Autowired private DetallePlanillaService detallePlanillaService;
    @Autowired private MovimientoPlanillaService movimientoPlanillaService;
    @Autowired private HistorialPuestoService historialPuestoService; // Para obtener el puesto en el periodo

    /**
     * Recupera una lista de todas las planillas.
     * @return Una lista de objetos Planilla.
     */
    @Override
    public List<Planilla> getAllPlanillas() {
        return planillaRepository.findAll();
    }

    /**
     * Recupera una planilla por su ID.
     * @param id El ID de la planilla.
     * @return Un Optional que contiene la Planilla si se encuentra, o vacío si no se encuentra.
     */
    @Override
    public Optional<Planilla> getPlanillaById(Integer idPlanilla) {
        return planillaRepository.findById(idPlanilla);
    }

    /**
     * Guarda una nueva planilla o actualiza una existente.
     * @param planilla El objeto Planilla a guardar/actualizar.
     * @return La Planilla guardada/actualizada.
     */
    @Override
    public Planilla savePlanilla(Planilla planilla) {
        return planillaRepository.save(planilla);
    }

    /**
     * Elimina una planilla por su ID.
     * @param id El ID de la planilla a eliminar.
     */
    @Override
    public void deletePlanilla(Integer idPlanilla) {
        planillaRepository.deleteById(idPlanilla);
    }

    /**
     * Busca una planilla por mes, año y tipo.
     * @param mes El mes de la planilla.
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla (ej. MENSUAL, CTS, GRATIFICACION).
     * @return Un Optional que contiene la Planilla encontrada, o vacío si no se encuentra.
     */
    @Override
    public Optional<Planilla> getPlanillaByMesAnioAndTipo(Integer mes, Integer anio, TipoPlanilla tipoPlanilla) {
        return planillaRepository.findByMesAndAnioAndTipoPlanilla(mes, anio, tipoPlanilla);
    }

    /**
     * Busca todas las planillas generadas en un rango de fechas.
     * @param fechaInicio La fecha de inicio del rango (inclusive).
     * @param fechaFin La fecha de fin del rango (inclusive).
     * @return Una lista de planillas generadas en el rango especificado.
     */
    @Override
    public List<Planilla> getPlanillasByFechaGeneradaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return planillaRepository.findByFechaGeneradaBetween(fechaInicio, fechaFin);
    }

    /**
     * Busca todas las planillas de un tipo específico para un año dado.
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla.
     * @return Una lista de planillas que coinciden con el año y tipo.
     */
    @Override
    public List<Planilla> getPlanillasByAnioAndTipo(Integer anio, TipoPlanilla tipoPlanilla) {
        return planillaRepository.findByAnioAndTipoPlanilla(anio, tipoPlanilla);
    }

    /**
     * Genera y calcula una nueva planilla para un mes, año y tipo específicos.
     * Este es el método central para el cálculo de planillas, orquestando todas las dependencias.
     *
     * @param mes El mes de la planilla (1-12).
     * @param anio El año de la planilla.
     * @param tipoPlanilla El tipo de planilla a generar (MENSUAL, CTS, GRATIFICACION, LBS, VACACION).
     * @return La Planilla generada con todos sus detalles.
     * @throws IllegalStateException Si la planilla para el período y tipo ya existe.
     * @throws RuntimeException Si ocurren errores durante el cálculo.
     */
    @Override
    @Transactional // Asegura que toda la operación sea atómica (commit o rollback)
    public Planilla generatePlanilla(Integer mes, Integer anio, TipoPlanilla tipoPlanilla) {
        // 1. Validar si la planilla para ese mes/anio/tipo ya existe
        if (getPlanillaByMesAnioAndTipo(mes, anio, tipoPlanilla).isPresent()) {
            throw new IllegalStateException("Ya existe una planilla de tipo " + tipoPlanilla +
                                            " para el mes " + mes + " y año " + anio);
        }

        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaInicioPeriodo = LocalDate.of(anio, mes, 1);
        LocalDate fechaFinPeriodo = fechaInicioPeriodo.withDayOfMonth(fechaInicioPeriodo.lengthOfMonth());

        // 2. Obtener los parámetros legales vigentes (RMV, UIT) para el periodo
        // Usar getParametroLegalByCodigoAndFechaVigencia
        ParametroLegal rmv = parametroLegalService.getParametroLegalByCodigoAndFechaVigencia("RMV", fechaFinPeriodo)
                                                     .orElseThrow(() -> new RuntimeException("RMV no encontrado para la fecha: " + fechaFinPeriodo));
        ParametroLegal uit = parametroLegalService.getParametroLegalByCodigoAndFechaVigencia("UIT", fechaFinPeriodo)
                                                     .orElseThrow(() -> new RuntimeException("UIT no encontrado para la fecha: " + fechaFinPeriodo));

        // 3. Crear la cabecera de la planilla
        Planilla nuevaPlanilla = new Planilla();
        nuevaPlanilla.setMes(mes);
        nuevaPlanilla.setAnio(anio);
        nuevaPlanilla.setTipoPlanilla(tipoPlanilla);
        nuevaPlanilla.setFechaGenerada(fechaActual);
        nuevaPlanilla.setParamRmv(rmv); // Asociar la RMV vigente
        nuevaPlanilla = planillaRepository.save(nuevaPlanilla); // Guardar para obtener el ID

        // 4. Obtener todos los conceptos de pago (Ingresos, Descuentos, Aportes Empleador)
        List<ConceptoPago> conceptosIngresos = conceptoPagoService.getConceptosPagoByTipo(TipoConcepto.INGRESO);
        List<ConceptoPago> conceptosDescuentos = conceptoPagoService.getConceptosPagoByTipo(TipoConcepto.DESCUENTO);
        List<ConceptoPago> conceptosAportesEmpleador = conceptoPagoService.getConceptosPagoByTipo(TipoConcepto.APORTE_EMPLEADOR);

        // 5. Obtener los empleados activos para el periodo
        // Para una planilla mensual, consideramos los empleados activos en la fecha de generación
        List<Empleado> empleadosActivos = empleadoService.getActiveEmpleados();

        if (empleadosActivos.isEmpty()) {
            // Considerar si esto es un error o simplemente no hay planilla que generar
            throw new RuntimeException("No hay empleados activos para generar la planilla.");
        }

        // 6. Iterar sobre cada empleado para calcular su detalle de planilla
        for (Empleado empleado : empleadosActivos) {
            // Lógica compleja para calcular el detalle de planilla para CADA EMPLEADO
            DetallePlanilla detalle = new DetallePlanilla();
            detalle.setPlanilla(nuevaPlanilla);
            detalle.setEmpleado(empleado);

            // Obtener el puesto actual o histórico del empleado para el periodo de la planilla
            Optional<HistorialPuesto> puestoHistoricoOpt = historialPuestoService.getPuestoByEmpleadoAndDate(empleado, fechaFinPeriodo);
            Puesto puestoActual = puestoHistoricoOpt.map(HistorialPuesto::getPuesto)
                                                   .orElseThrow(() -> new RuntimeException("No se encontró puesto para el empleado " + empleado.getNombres() + " " + empleado.getApellidos() + " para el periodo de planilla."));

            // Sueldo Base (del puesto actual del empleado)
            BigDecimal sueldoBase = puestoActual.getSalarioBase();
            detalle.setSueldoBase(sueldoBase);

            // Asignación Familiar (si aplica)
            BigDecimal asignacionFamiliar = BigDecimal.ZERO;
            if (empleado.getTieneHijosCalificados() != null && empleado.getTieneHijosCalificados()) {
                // Asignación Familiar = 10% de la RMV en Perú
                asignacionFamiliar = rmv.getValor().multiply(BigDecimal.valueOf(0.10));
            }
            detalle.setAsignacionFamiliar(asignacionFamiliar);

            // Remuneración Computable Afecta (base para ONP/AFP, EsSalud)
            // Esto es un cálculo complejo que depende de cada concepto de ingreso.
            // Para simplificar, aquí se asume Sueldo Base + Asignación Familiar como base.
            // La lógica real debe iterar sobre los MovimientosPlanilla de ingresos remunerativos.
            BigDecimal remuneracionComputableAfecta = sueldoBase.add(asignacionFamiliar);
            detalle.setRemuneracionComputableAfecta(remuneracionComputableAfecta);

            // Inicializar totales
            BigDecimal totalIngresos = sueldoBase.add(asignacionFamiliar); // Sueldo base y asignación familiar son ingresos iniciales
            BigDecimal totalDescuentos = BigDecimal.ZERO;
            BigDecimal totalAportesEmpleador = BigDecimal.ZERO;

            // ***** Generar Movimientos de Planilla (Ingresos) *****
            for (ConceptoPago concepto : conceptosIngresos) {
                BigDecimal montoMovimiento = BigDecimal.ZERO;
                // Lógica de cálculo específica para cada tipo de concepto de INGRESO

                if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.MONTO_FIJO) {
                    montoMovimiento = concepto.getValorReferencial() != null ? concepto.getValorReferencial() : BigDecimal.ZERO;
                } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                    // Si es un porcentaje del sueldo base u otro valor
                    montoMovimiento = sueldoBase.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64), MathContext.DECIMAL64);
                } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.FORMULA_ESPECIAL) {
                    // Ejemplo: Calcular Horas Extras de las Asistencias
                    if (concepto.getNombreConcepto().equalsIgnoreCase("Horas Extras 25%")) {
                        List<Asistencia> asistenciasHE = asistenciaService.getAsistenciasByEmpleadoAndFechaBetween(empleado, fechaInicioPeriodo, fechaFinPeriodo);
                        BigDecimal totalHoras25 = asistenciasHE.stream()
                            .map(Asistencia::getHorasExtras25)
                            .filter(h -> h != null)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                        // Calcular tarifa por hora (Sueldo Base / (Jornada Laboral * 30 días))
                        BigDecimal horasEnMes = BigDecimal.valueOf(puestoActual.getJornadaLaboralHoras()).multiply(BigDecimal.valueOf(30));
                        BigDecimal hourlyRate = sueldoBase.divide(horasEnMes, MathContext.DECIMAL64);

                        // Calcular monto de horas extras (total_horas * tarifa_por_hora * 1.25)
                        montoMovimiento = totalHoras25.multiply(hourlyRate, MathContext.DECIMAL64)
                                                     .multiply(BigDecimal.valueOf(1.25), MathContext.DECIMAL64);
                    }
                    // Añadir más lógica para otras fórmulas especiales
                }

                if (montoMovimiento.compareTo(BigDecimal.ZERO) > 0) {
                    MovimientoPlanilla mov = new MovimientoPlanilla();
                    mov.setDetallePlanilla(detalle); // El detalle se establecerá una vez guardado
                    mov.setConcepto(concepto);
                    mov.setMonto(montoMovimiento);
                    // No guardar aquí, se guardarán en cascada o después de guardar el detalle
                    totalIngresos = totalIngresos.add(montoMovimiento);
                }
            }
            detalle.setTotalIngresosAdicionales(totalIngresos.subtract(sueldoBase.add(asignacionFamiliar))); // Los ingresos adicionales son el total menos el sueldo base y la asignación familiar
            detalle.setSueldoBruto(totalIngresos);


            // ***** Generar Movimientos de Planilla (Descuentos) *****
            for (ConceptoPago concepto : conceptosDescuentos) {
                BigDecimal montoDescuento = BigDecimal.ZERO;
                // Lógica de cálculo específica para cada tipo de concepto de DESCUENTO
                if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                    if (concepto.getNombreConcepto().equalsIgnoreCase("Aporte AFP") && empleado.getSistemaPensiones() == Empleado.SistemaPensiones.AFP) {
                        // El porcentaje de AFP varía (fondo, comisión, seguro). Simplificando:
                        montoDescuento = remuneracionComputableAfecta.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64), MathContext.DECIMAL64);
                    } else if (concepto.getNombreConcepto().equalsIgnoreCase("Aporte ONP") && empleado.getSistemaPensiones() == Empleado.SistemaPensiones.ONP) {
                        montoDescuento = remuneracionComputableAfecta.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64), MathContext.DECIMAL64); // Usualmente 13%
                    }
                } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.MONTO_FIJO) {
                    montoDescuento = concepto.getValorReferencial() != null ? concepto.getValorReferencial() : BigDecimal.ZERO;
                } else if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.FORMULA_ESPECIAL) {
                    // Ejemplo: Descuento por Tardanza/Ausencia
                    if (concepto.getNombreConcepto().equalsIgnoreCase("Descuento Tardanzas")) {
                        List<Asistencia> asistenciasTardanzas = asistenciaService.getAsistenciasByEmpleadoAndFechaBetweenAndEstados(
                            empleado, fechaInicioPeriodo, fechaFinPeriodo, List.of(Asistencia.EstadoAsistencia.TARDANZA, Asistencia.EstadoAsistencia.AUSENTE));

                        // Lógica para calcular el descuento monetario basado en minutos de tardanza o días ausentes.
                        // Calcular costo por minuto (Sueldo Base / (Jornada Laboral * 60 minutos * 30 días))
                        BigDecimal minutosEnMes = BigDecimal.valueOf(puestoActual.getJornadaLaboralHoras()).multiply(BigDecimal.valueOf(60)).multiply(BigDecimal.valueOf(30));
                        BigDecimal costPerMinute = sueldoBase.divide(minutosEnMes, MathContext.DECIMAL64);

                        Integer totalMinutosTardanza = asistenciasTardanzas.stream()
                            .map(Asistencia::getMinutosTardanza)
                            .filter(m -> m != null)
                            .reduce(0, Integer::sum);
                        montoDescuento = BigDecimal.valueOf(totalMinutosTardanza).multiply(costPerMinute, MathContext.DECIMAL64);
                    }
                    // Lógica para otros descuentos especiales (adelantos, préstamos, Impuesto a la Renta - Quinta Categoría)
                    // El cálculo del Impuesto a la Renta (Quinta Categoría) es muy complejo y requerirá su propia función,
                    // considerando proyecciones anuales, deducciones y la UIT. Es el más complicado.
                }

                if (montoDescuento.compareTo(BigDecimal.ZERO) > 0) {
                    MovimientoPlanilla mov = new MovimientoPlanilla();
                    mov.setDetallePlanilla(detalle);
                    mov.setConcepto(concepto);
                    mov.setMonto(montoDescuento);
                    // No guardar aquí
                    totalDescuentos = totalDescuentos.add(montoDescuento);
                }
            }
            detalle.setTotalDescuentos(totalDescuentos);
            detalle.setSueldoNeto(detalle.getSueldoBruto().subtract(totalDescuentos));

            // ***** Generar Movimientos de Planilla (Aportes del Empleador) *****
            for (ConceptoPago concepto : conceptosAportesEmpleador) {
                BigDecimal montoAporte = BigDecimal.ZERO;
                if (concepto.getMetodoCalculo() == ConceptoPago.MetodoCalculo.PORCENTAJE) {
                    if (concepto.getNombreConcepto().equalsIgnoreCase("EsSalud")) {
                        // EsSalud es el 9% de la remuneración computable afecta
                        montoAporte = remuneracionComputableAfecta.multiply(concepto.getValorReferencial().divide(BigDecimal.valueOf(100), MathContext.DECIMAL64), MathContext.DECIMAL64);
                    }
                    // Otros aportes del empleador como SCTR, Senati, etc.
                }

                if (montoAporte.compareTo(BigDecimal.ZERO) > 0) {
                    MovimientoPlanilla mov = new MovimientoPlanilla();
                    mov.setDetallePlanilla(detalle);
                    mov.setConcepto(concepto);
                    mov.setMonto(montoAporte);
                    // No guardar aquí
                    totalAportesEmpleador = totalAportesEmpleador.add(montoAporte);
                }
            }
            detalle.setTotalAportesEmpleador(totalAportesEmpleador);

            // Guardar el DetallePlanilla (esto también guardará los MovimientosPlanilla si la cascada está configurada)
            detallePlanillaService.saveDetallePlanilla(detalle);

            // IMPORTANTE: Los MovimientoPlanilla que creaste dentro de los bucles
            // necesitan ser persistidos. Si no tienes cascada en DetallePlanilla
            // para MovimientoPlanilla, necesitarías una lista de MovimientoPlanilla
            // en DetallePlanilla y guardarlos junto con el detalle, o guardarlos aquí.
            // Para simplificar, asumo que saveDetallePlanilla manejará la cascada
            // si la relación OneToMany está configurada con CascadeType.ALL en DetallePlanilla.
            // Si no, la lista de MovimientoPlanilla debería agregarse al detalle
            // y luego se guarda el detalle.
        }

        return nuevaPlanilla;
    }
}
