package com.textilima.textilima.service.impl;

// Importaciones de clases de entities.o - ¡VERIFICA SUS PAQUETES!
import com.textilima.textilima.entities.Planilla;
import com.textilima.textilima.entities.DetallePlanilla;
import com.textilima.textilima.entities.Empleado;
import com.textilima.textilima.entities.ConceptoPago;
import com.textilima.textilima.entities.Bono;
import com.textilima.textilima.entities.Descuento;

// Importaciones de interfaces de repositorio - ¡VERIFICA SUS PAQUETES!
import com.textilima.textilima.repository.PlanillaRepository;
import com.textilima.textilima.repository.DetallePlanillaRepository;
import com.textilima.textilima.repository.BonoRepository;
import com.textilima.textilima.repository.DescuentoRepository;

// Importaciones de interfaces de servicio - ¡VERIFICA SUS PAQUETES!
import com.textilima.textilima.service.PlanillaService;
import com.textilima.textilima.service.EmpleadoService;
import com.textilima.textilima.service.ConceptoPagoService;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Importaciones de Java estándar
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanillaServiceImpl implements PlanillaService {

       private final EmpleadoService empleadoService;
    private final PlanillaRepository planillaRepository;
    private final DetallePlanillaRepository detallePlanillaRepository;
    private final ConceptoPagoService conceptoPagoService;
    private final BonoRepository bonoRepository;
    private final DescuentoRepository descuentoRepository;

    private static final BigDecimal RMV = BigDecimal.valueOf(1025.00);
    private static final BigDecimal ONP_TASA = BigDecimal.valueOf(0.13);
    private static final BigDecimal AFP_TASA_FONDO_AVG = BigDecimal.valueOf(0.10);
    private static final BigDecimal AFP_TASA_COMISION_AVG = BigDecimal.valueOf(0.015);
    private static final BigDecimal AFP_TASA_PRIMA_SEGURO_AVG = BigDecimal.valueOf(0.017);
    private static final BigDecimal ESSALUD_TASA = BigDecimal.valueOf(0.09);
    private static final BigDecimal ASIGNACION_FAMILIAR_VALOR = RMV.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);

    private static final int DECIMAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Autowired
    public PlanillaServiceImpl(EmpleadoService empleadoService,
                               PlanillaRepository planillaRepository,
                               DetallePlanillaRepository detallePlanillaRepository,
                               ConceptoPagoService conceptoPagoService,
                               BonoRepository bonoRepository,
                               DescuentoRepository descuentoRepository) {
        this.empleadoService = empleadoService;
        this.planillaRepository = planillaRepository;
        this.detallePlanillaRepository = detallePlanillaRepository;
        this.conceptoPagoService = conceptoPagoService;
        this.bonoRepository = bonoRepository;
        this.descuentoRepository = descuentoRepository;
    }

    @Override
    @Transactional
    public Planilla generarPlanillaMensual(Integer mes, Integer anio) {
        Optional<Planilla> existingPlanilla = planillaRepository.findByMesAndAnio(mes, anio);
        if (existingPlanilla.isPresent()) {
            throw new IllegalStateException("Ya existe una planilla mensual para el mes " + mes + " y año " + anio);
        }

        Planilla planilla = new Planilla();
        planilla.setMes(mes);
        planilla.setAnio(anio);
        planilla.setTipoPlanilla(Planilla.TipoPlanilla.MENSUAL);
        planilla.setFechaGenerada(LocalDate.now());
        planillaRepository.save(planilla);

        List<Empleado> empleadosActivos = empleadoService.listarTodosLosEmpleados().stream()
                                            .filter(Empleado::getEstado)
                                            .collect(Collectors.toList());
        
        List<DetallePlanilla> detallesPlanillaList = new ArrayList<>();

        ConceptoPago conceptoSueldo = getOrCreateConcepto("Sueldo Base", "0101", ConceptoPago.TipoConcepto.INGRESO, true, false, true, BigDecimal.ZERO, true, true, true, true, null);
        ConceptoPago conceptoAsignacionFamiliar = getOrCreateConcepto("Asignación Familiar", "0105", ConceptoPago.TipoConcepto.INGRESO, true, false, true, BigDecimal.ZERO, true, true, true, true, null);
        ConceptoPago conceptoONP = getOrCreateConcepto("Aporte ONP", "0601", ConceptoPago.TipoConcepto.APORTE_EMPLEADO, false, true, false, BigDecimal.ZERO, false, true, false, false, null);
        ConceptoPago conceptoAFP_Fondo = getOrCreateConcepto("Aporte AFP Fondo", "0605", ConceptoPago.TipoConcepto.APORTE_EMPLEADO, false, true, false, BigDecimal.ZERO, false, false, true, false, null);
        ConceptoPago conceptoAFP_Comision = getOrCreateConcepto("Aporte AFP Comisión", "0607", ConceptoPago.TipoConcepto.APORTE_EMPLEADO, false, true, false, BigDecimal.ZERO, false, false, true, false, null);
        ConceptoPago conceptoAFP_Prima = getOrCreateConcepto("Aporte AFP Prima Seguro", "0606", ConceptoPago.TipoConcepto.APORTE_EMPLEADO, false, true, false, BigDecimal.ZERO, false, false, true, false, null);
        ConceptoPago conceptoEsSalud = getOrCreateConcepto("Aporte EsSalud", "0801", ConceptoPago.TipoConcepto.APORTE_EMPLEADOR, false, true, false, BigDecimal.ZERO, false, false, false, true, null);


        for (Empleado empleado : empleadosActivos) {
            DetallePlanilla detalle = new DetallePlanilla();
            detalle.setPlanilla(planilla);
            detalle.setEmpleado(empleado);
            detalle.setConceptoPago(null); 

            BigDecimal sueldoBruto = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            BigDecimal remuneracionComputable = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            BigDecimal asignacionFamiliar = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            BigDecimal totalBonosAcumulado = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            BigDecimal totalDescuentosAcumulado = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            BigDecimal aportePensionEmpleado = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            BigDecimal impuestoRenta5ta = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE); 
            BigDecimal sueldoNeto = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            BigDecimal aporteEssaludEmpleador = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);

            List<Bono> bonosParaGuardar = new ArrayList<>();
            List<Descuento> descuentosParaGuardar = new ArrayList<>();


            BigDecimal salarioBasePuesto = empleado.getPuesto().getSalarioBase() != null ? empleado.getPuesto().getSalarioBase() : BigDecimal.ZERO;
            
            sueldoBruto = sueldoBruto.add(salarioBasePuesto); 
            remuneracionComputable = remuneracionComputable.add(salarioBasePuesto);

            Bono sueldoBono = new Bono();
            sueldoBono.setConceptoPago(conceptoSueldo);
            sueldoBono.setMonto(salarioBasePuesto);
            bonosParaGuardar.add(sueldoBono);
            
            if ("Regimen General".equalsIgnoreCase(empleado.getRegimenLaboral()) && 
                empleado.getNumeroHijos() != null && empleado.getNumeroHijos() > 0 && 
                salarioBasePuesto.compareTo(RMV) >= 0) {
                
                asignacionFamiliar = ASIGNACION_FAMILIAR_VALOR; 
                sueldoBruto = sueldoBruto.add(asignacionFamiliar); 
                remuneracionComputable = remuneracionComputable.add(asignacionFamiliar);

                Bono afBono = new Bono();
                afBono.setConceptoPago(conceptoAsignacionFamiliar);
                afBono.setMonto(asignacionFamiliar);
                bonosParaGuardar.add(afBono);
            }

            if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.ONP) {
                aportePensionEmpleado = remuneracionComputable.multiply(ONP_TASA).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                
                Descuento onpDescuento = new Descuento();
                onpDescuento.setConceptoPago(conceptoONP);
                onpDescuento.setMonto(aportePensionEmpleado);
                descuentosParaGuardar.add(onpDescuento);

            } else if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.AFP) {
                BigDecimal fondo = remuneracionComputable.multiply(AFP_TASA_FONDO_AVG).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                BigDecimal comision = remuneracionComputable.multiply(AFP_TASA_COMISION_AVG).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                BigDecimal primaSeguro = remuneracionComputable.multiply(AFP_TASA_PRIMA_SEGURO_AVG).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                
                aportePensionEmpleado = fondo.add(comision).add(primaSeguro);

                Descuento afpFondoDesc = new Descuento();
                afpFondoDesc.setConceptoPago(conceptoAFP_Fondo);
                afpFondoDesc.setMonto(fondo);
                descuentosParaGuardar.add(afpFondoDesc);

                Descuento afpComisionDesc = new Descuento();
                afpComisionDesc.setConceptoPago(conceptoAFP_Comision);
                afpComisionDesc.setMonto(comision);
                descuentosParaGuardar.add(afpComisionDesc);

                Descuento afpPrimaDesc = new Descuento();
                afpPrimaDesc.setConceptoPago(conceptoAFP_Prima);
                afpPrimaDesc.setMonto(primaSeguro);
                descuentosParaGuardar.add(afpPrimaDesc);
            }
            
            impuestoRenta5ta = BigDecimal.ZERO.setScale(DECIMAL_SCALE, ROUNDING_MODE);
            
            totalBonosAcumulado = bonosParaGuardar.stream()
                                    .map(Bono::getMonto)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    .setScale(DECIMAL_SCALE, ROUNDING_MODE);

            totalDescuentosAcumulado = descuentosParaGuardar.stream()
                                        .map(Descuento::getMonto)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                                        .setScale(DECIMAL_SCALE, ROUNDING_MODE);
            
            totalDescuentosAcumulado = totalDescuentosAcumulado.add(impuestoRenta5ta);

            aporteEssaludEmpleador = remuneracionComputable.multiply(ESSALUD_TASA).setScale(DECIMAL_SCALE, ROUNDING_MODE);


            sueldoNeto = sueldoBruto.subtract(totalDescuentosAcumulado); 
            
            detalle.setSueldoBruto(sueldoBruto);
            detalle.setRemuneracionComputable(remuneracionComputable);
            detalle.setAsignacionFamiliar(asignacionFamiliar);
            detalle.setTotalBonos(totalBonosAcumulado);
            detalle.setTotalDescuentos(totalDescuentosAcumulado);
            detalle.setAportePensionEmpleado(aportePensionEmpleado);
            detalle.setImpuestoRenta5ta(impuestoRenta5ta);
            detalle.setSueldoNeto(sueldoNeto);
            detalle.setAporteEssaludEmpleador(aporteEssaludEmpleador);

            detallePlanillaRepository.save(detalle);

            for (Bono bono : bonosParaGuardar) {
                bono.setDetallePlanilla(detalle);
                bonoRepository.save(bono);
                detalle.getBonos().add(bono);
            }
            for (Descuento descuento : descuentosParaGuardar) {
                descuento.setDetallePlanilla(detalle);
                descuentoRepository.save(descuento);
                detalle.getDescuentos().add(descuento);
            }

            detallesPlanillaList.add(detalle);
        }

        planilla.setDetalles(detallesPlanillaList);
        
        return planilla;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Planilla> getAllPlanillas() {
        List<Planilla> planillas = planillaRepository.findAll();
        planillas.forEach(p -> {
            if (p.getDetalles() != null) {
                p.getDetalles().forEach(detalle -> {
                    // Forzar carga de Planilla y Empleado (ya deberían estar cargados por la transacción)
                    if (detalle.getPlanilla() != null) detalle.getPlanilla().getIdPlanilla(); // Acceso básico
                    if (detalle.getEmpleado() != null) {
                        detalle.getEmpleado().getNombres(); // Forzar carga del Empleado
                        // ¡¡¡CRÍTICO!!! Forzar carga de numeroHijos
                        detalle.getEmpleado().getNumeroHijos(); 
                        detalle.getEmpleado().getPuesto().getNombrePuesto(); // Forzar carga del Puesto
                    }
                    if (detalle.getConceptoPago() != null) detalle.getConceptoPago().getNombreConcepto();
                    
                    // Forzar carga de las colecciones de bonos y descuentos
                    if (detalle.getBonos() != null) detalle.getBonos().size();
                    if (detalle.getDescuentos() != null) detalle.getDescuentos().size();
                });
            }
        });
        return planillas;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Planilla> getPlanillaById(Integer id) {
        Optional<Planilla> planillaOptional = planillaRepository.findById(id);
        planillaOptional.ifPresent(p -> {
            if (p.getDetalles() != null) {
                p.getDetalles().forEach(detalle -> {
                    // Forzar carga de Planilla y Empleado
                    if (detalle.getPlanilla() != null) detalle.getPlanilla().getIdPlanilla();
                    if (detalle.getEmpleado() != null) {
                        detalle.getEmpleado().getNombres();
                        // ¡¡¡CRÍTICO!!! Forzar carga de numeroHijos
                        detalle.getEmpleado().getNumeroHijos(); 
                        detalle.getEmpleado().getPuesto().getNombrePuesto(); // Forzar carga del Puesto
                    }
                    if (detalle.getConceptoPago() != null) detalle.getConceptoPago().getNombreConcepto();

                    // Forzar carga de las colecciones de bonos y descuentos
                    if (detalle.getBonos() != null) detalle.getBonos().size();
                    if (detalle.getDescuentos() != null) detalle.getDescuentos().size();
                });
            }
        });
        return planillaOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Planilla> findByMesAndAnio(Integer mes, Integer anio) {
        return planillaRepository.findByMesAndAnio(mes, anio);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        planillaRepository.deleteById(id);
    }

    private ConceptoPago getOrCreateConcepto(String nombre, String codigo, ConceptoPago.TipoConcepto tipo,
                                             boolean esRemunerativo, boolean esObligatorio,
                                             boolean aplicaMontoFijo, BigDecimal valor,
                                             boolean afectoIR, boolean afectoONP, boolean afectoAFP, boolean afectoEssalud,
                                             String descripcion) {
        return conceptoPagoService.findByNombreConcepto(nombre)
                .orElseGet(() -> {
                    ConceptoPago nuevoConcepto = new ConceptoPago();
                    nuevoConcepto.setNombreConcepto(nombre);
                    nuevoConcepto.setCodigoSunat(codigo);
                    nuevoConcepto.setTipo(tipo);
                    nuevoConcepto.setEsRemunerativo(esRemunerativo);
                    nuevoConcepto.setEsObligatorio(esObligatorio);
                    nuevoConcepto.setAplicaMontoFijo(aplicaMontoFijo);
                    nuevoConcepto.setValor(valor); 
                    nuevoConcepto.setAfectoIR(afectoIR);
                    nuevoConcepto.setAfectoONP(afectoONP);
                    nuevoConcepto.setAfectoAFP(afectoAFP);
                    nuevoConcepto.setAfectoEssalud(afectoEssalud);
                    nuevoConcepto.setDescripcion(descripcion);
                    return conceptoPagoService.save(nuevoConcepto);
                });
    }
}
