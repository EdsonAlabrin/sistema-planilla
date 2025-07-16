package com.textilima.textilima.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.enums.*;
import com.github.javafaker.Faker;
import com.textilima.textilima.model.Asistencia;
import com.textilima.textilima.model.Asistencia.EstadoAsistencia;
import com.textilima.textilima.model.Banco;
import com.textilima.textilima.model.ConceptoPago;
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;
import com.textilima.textilima.model.DetallePlanilla;
import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Empleado.SistemaPensiones;
import com.textilima.textilima.model.HistorialPuesto;
import com.textilima.textilima.model.MovimientoPlanilla;
import com.textilima.textilima.model.ParametroLegal;
import com.textilima.textilima.model.Planilla;
import com.textilima.textilima.model.Planilla.TipoPlanilla;
import com.textilima.textilima.model.Puesto;
import com.textilima.textilima.repository.AsistenciaRepository;
import com.textilima.textilima.repository.BancoRepository;
import com.textilima.textilima.repository.ConceptoPagoRepository;
import com.textilima.textilima.repository.DetallePlanillaRepository;
import com.textilima.textilima.repository.EmpleadoRepository;
import com.textilima.textilima.repository.HistorialPuestoRepository;
import com.textilima.textilima.repository.MovimientoPlanillaRepository;
import com.textilima.textilima.repository.ParametroLegalRepository;
import com.textilima.textilima.repository.PlanillaRepository;
import com.textilima.textilima.repository.PuestoRepository;

@Service
public class DataSeederService {
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private PuestoRepository puestoRepository;
    @Autowired
    private ConceptoPagoRepository conceptoPagoRepository;
    @Autowired
    private PlanillaRepository planillaRepository;
    @Autowired
    private DetallePlanillaRepository detallePlanillaRepository;
    @Autowired
    private MovimientoPlanillaRepository movimientoPlanillaRepository;
    @Autowired // ¡Inyectar el nuevo repositorio de Banco!
    private BancoRepository bancoRepository;
    @Autowired
    private ParametroLegalRepository parametroLegalRepository;
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    @Autowired private HistorialPuestoRepository historialPuestoRepository;

    private Faker faker = new Faker(Locale.of("es", "PE")); // Faker para Perú
    private Random random = new Random();

    private static final BigDecimal RMV_DEFAULT = new BigDecimal("1130.00");
    private static final BigDecimal ASIGNACION_FAMILIAR_VALOR = new BigDecimal("113.00");
    
    private static List<Puesto> puestosExistentes = new ArrayList<>();
    
    public void seedConceptosPago() {
        System.out.println("Sembrando conceptos de pago...");

        // Usaremos una lista de objetos anónimos o una clase interna para definir los
        // conceptos fácilmente
        class ConceptoData {
            String codigoSunat;
            String nombreConcepto;
            TipoConcepto tipo;
            ConceptoPago.MetodoCalculo metodoCalculo;
            Boolean esRemunerativo;
            BigDecimal valorReferencial;
            Boolean afectoOnp;
            Boolean afectoAfp;
            Boolean afectoEssalud;
            String descripcion;

            public ConceptoData(String codigoSunat, String nombreConcepto, TipoConcepto tipo,
                    ConceptoPago.MetodoCalculo metodoCalculo,
                    Boolean esRemunerativo, BigDecimal valorReferencial, Boolean afectoOnp, Boolean afectoAfp,
                    Boolean afectoEssalud, String descripcion) {
                this.codigoSunat = codigoSunat;
                this.nombreConcepto = nombreConcepto;
                this.tipo = tipo;
                this.metodoCalculo = metodoCalculo;
                this.esRemunerativo = esRemunerativo;
                this.valorReferencial = valorReferencial;
                this.afectoOnp = afectoOnp;
                this.afectoAfp = afectoAfp;
                this.afectoEssalud = afectoEssalud;
                this.descripcion = descripcion;
            }
        }

        List<ConceptoData> conceptosToSeed = new ArrayList<>();

        // Ingresos
        conceptosToSeed.add(
                new ConceptoData("0100", "Sueldo Base", TipoConcepto.INGRESO, ConceptoPago.MetodoCalculo.MONTO_FIJO,
                        true, BigDecimal.ZERO, true, true, true, "Remuneración base mensual"));
        conceptosToSeed.add(new ConceptoData("0101", "Asignación Familiar", TipoConcepto.INGRESO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, true, ASIGNACION_FAMILIAR_VALOR, true, true, true,
                "Asignación por hijos"));
        conceptosToSeed
                .add(new ConceptoData("0200", "Comisiones", TipoConcepto.INGRESO, ConceptoPago.MetodoCalculo.MONTO_FIJO,
                        true, BigDecimal.ZERO, true, true, true, "Comisiones por ventas"));
            conceptosToSeed.add(
                    new ConceptoData("0202", "Horas Extras 25%", TipoConcepto.INGRESO, ConceptoPago.MetodoCalculo.PORCENTAJE,
                            true, new BigDecimal("1.25"), true, true, true, "Pago por horas extras al 25% (valor referencial es el 125%)")); // El valor referencial es 125%
            conceptosToSeed.add(
                    new ConceptoData("0203", "Horas Extras 35%", TipoConcepto.INGRESO, ConceptoPago.MetodoCalculo.PORCENTAJE, 
                            true, new BigDecimal("1.35"), true, true, true, "Pago por horas extras al 35% (valor referencial es el 135%)"));
        conceptosToSeed.add(new ConceptoData("0301", "Gratificación Ordinaria", TipoConcepto.INGRESO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, true, BigDecimal.ZERO, false, false, false,
                "Gratificación de Julio/Diciembre (no afecta a Essalud, etc.)"));
        conceptosToSeed.add(new ConceptoData("0307", "Bonificación Extraordinaria (Ley)", TipoConcepto.INGRESO,
                ConceptoPago.MetodoCalculo.PORCENTAJE, false, new BigDecimal("9"), false, false, false,
                "Bonificación Ley 30334 (9% sobre gratificación)"));
        conceptosToSeed.add(new ConceptoData("0600", "CTS", TipoConcepto.INGRESO, ConceptoPago.MetodoCalculo.MONTO_FIJO,
                false, BigDecimal.ZERO, false, false, false, "Compensación por Tiempo de Servicios"));
        conceptosToSeed.add(new ConceptoData("0400", "Movilidad supeditada a asistencia", TipoConcepto.INGRESO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, false, new BigDecimal("50.00"), false, false, false,
                "Movilidad que no es remunerativa"));
        conceptosToSeed.add(new ConceptoData("0402", "Condición de Trabajo (Alimentos)", TipoConcepto.INGRESO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, false, new BigDecimal("100.00"), false, false, false,
                "Condición de trabajo no remunerativa"));
        conceptosToSeed.add(new ConceptoData("9999", "Liquidación de Beneficios Sociales", TipoConcepto.INGRESO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, false, BigDecimal.ZERO, false, false, false,
                "Liquidación por cese"));

        // Descuentos
        conceptosToSeed.add(
                new ConceptoData("0601", "Aporte ONP", TipoConcepto.DESCUENTO, ConceptoPago.MetodoCalculo.PORCENTAJE,
                        null, new BigDecimal("13.00"), null, null, null, "Aporte al Sistema Nacional de Pensiones"));
        conceptosToSeed.add(new ConceptoData("0602", "Aporte AFP Fondo", TipoConcepto.DESCUENTO,
                ConceptoPago.MetodoCalculo.PORCENTAJE, null, new BigDecimal("10.00"), null, null, null,
                "Aporte al fondo de AFP"));
        conceptosToSeed.add(
                new ConceptoData("0603", "Comisión AFP", TipoConcepto.DESCUENTO, ConceptoPago.MetodoCalculo.PORCENTAJE,
                        null, new BigDecimal("1.50"), null, null, null, "Comisión por administración de AFP"));
        conceptosToSeed.add(new ConceptoData("0604", "Prima Seguro AFP", TipoConcepto.DESCUENTO,
                ConceptoPago.MetodoCalculo.PORCENTAJE, null, new BigDecimal("1.74"), null, null, null,
                "Prima de seguro de AFP"));
        conceptosToSeed.add(new ConceptoData("0501", "Impuesto a la Renta 5ta Categoría", TipoConcepto.DESCUENTO,
                ConceptoPago.MetodoCalculo.FORMULA_ESPECIAL, null, BigDecimal.ZERO, null, null, null,
                "Impuesto a la Renta de 5ta Categoría"));
        conceptosToSeed.add(new ConceptoData("0502", "Descuento por Tardanza", TipoConcepto.DESCUENTO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, null, BigDecimal.ZERO, null, null, null,
                "Descuento por tardanzas o inasistencias"));
        conceptosToSeed.add(new ConceptoData("0505", "Descuento por Ausencia", TipoConcepto.DESCUENTO,
            ConceptoPago.MetodoCalculo.MONTO_FIJO, null, BigDecimal.ZERO, null, null, null,
            "Descuento por días de inasistencia"));
        conceptosToSeed.add(new ConceptoData("0503", "Descuento por Adelanto", TipoConcepto.DESCUENTO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, null, BigDecimal.ZERO, null, null, null, "Adelantos de sueldo"));
        conceptosToSeed.add(new ConceptoData("0504", "Embargo Judicial", TipoConcepto.DESCUENTO,
                ConceptoPago.MetodoCalculo.MONTO_FIJO, null, BigDecimal.ZERO, null, null, null,
                "Embargo por mandato judicial (ej. alimentos)"));

        // Aportes Empleador
        conceptosToSeed.add(new ConceptoData("0801", "EsSalud", TipoConcepto.APORTE_EMPLEADOR,
                ConceptoPago.MetodoCalculo.PORCENTAJE, null, new BigDecimal("9.00"), null, null, null,
                "Aporte a EsSalud a cargo del empleador"));
        conceptosToSeed.add(new ConceptoData("0802", "SCTR Salud", TipoConcepto.APORTE_EMPLEADOR,
                ConceptoPago.MetodoCalculo.PORCENTAJE, null, new BigDecimal("0.75"), null, null, null,
                "Seguro Complementario de Trabajo de Riesgo - Salud"));
        conceptosToSeed.add(new ConceptoData("0803", "SCTR Pensión", TipoConcepto.APORTE_EMPLEADOR,
                ConceptoPago.MetodoCalculo.PORCENTAJE, null, new BigDecimal("0.50"), null, null, null,
                "Seguro Complementario de Trabajo de Riesgo - Pensión"));

        for (ConceptoData data : conceptosToSeed) {
            Optional<ConceptoPago> existingConcepto = conceptoPagoRepository.findByNombreConcepto(data.nombreConcepto);
            if (existingConcepto.isEmpty()) { // Si no existe, lo creamos
                ConceptoPago concepto = new ConceptoPago();
                concepto.setCodigoSunat(data.codigoSunat);
                concepto.setNombreConcepto(data.nombreConcepto);
                concepto.setTipo(data.tipo);
                concepto.setMetodoCalculo(data.metodoCalculo);
                concepto.setEsRemunerativo(data.esRemunerativo);
                concepto.setValorReferencial(data.valorReferencial);
                concepto.setAfectoOnp(data.afectoOnp);
                concepto.setAfectoAfp(data.afectoAfp);
                concepto.setAfectoEssalud(data.afectoEssalud);
                concepto.setDescripcion(data.descripcion);
                conceptoPagoRepository.save(concepto);
                System.out.println("  Concepto '" + data.nombreConcepto + "' sembrado.");
            } else {
                System.out.println("  Concepto '" + data.nombreConcepto + "' ya existe, omitiendo.");
                // Opcional: Actualizar campos si ya existe, pero para prototipos no suele ser
                // necesario
                // existingConcepto.get().setValorReferencial(data.valorReferencial);
                // conceptoPagoRepository.save(existingConcepto.get());
            }
        }
        System.out.println("Proceso de siembra de conceptos de pago finalizado.");
    }

    public List<Puesto> seedPuestos() {
        if (!puestosExistentes.isEmpty()) {
            System.out.println("Puestos ya sembrados, reutilizando existentes.");
            return puestosExistentes;
        }
        List<java.util.Map<String, Object>> puestosData = new ArrayList<>();
        
        // Puestos de Producción
        puestosData.add(createPuestoData("Operario de Confección", "Realiza tareas de confección de prendas en línea de producción.", "1200.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Cortador de Tela", "Encargado del trazado y corte de patrones de tela.", "1500.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Tejedor", "Opera máquinas de tejido para producir diferentes tipos de telas.", "1600.00", 8, "07:00", "16:00"));
        puestosData.add(createPuestoData("Bordador", "Maneja máquinas de bordado para aplicar diseños en prendas y textiles.", "1750.00", 8, "09:00", "18:00"));
        puestosData.add(createPuestoData("Ayudante de Producción", "Asiste en diversas tareas manuales y de soporte en el área de producción.", "1025.00", 8, "08:30", "17:30"));
        puestosData.add(createPuestoData("Encargado de Acabados", "Supervisa el proceso de planchado, etiquetado y empaquetado final de las prendas.", "1700.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Operario de Planchado", "Realiza el planchado de prendas terminadas y las prepara para el siguiente paso.", "1100.00", 8, "09:00", "18:00"));

        // Puestos de Supervisión
        puestosData.add(createPuestoData("Supervisor de Confección", "Supervisa al personal y el progreso en el área de confección, asegurando eficiencia.", "3200.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Jefe de Taller de Corte", "Gestiona las operaciones diarias del taller de corte y el personal a cargo.", "3800.00", 8, "07:00", "16:00"));
        puestosData.add(createPuestoData("Jefe de Producción Textil", "Líder de toda la línea de producción textil, desde materias primas hasta producto final.", "4500.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Inspector de Calidad", "Garantiza los estándares de calidad del producto a lo largo de todas las etapas de producción.", "2500.00", 8, "09:00", "18:00"));
        puestosData.add(createPuestoData("Encargado de Control de Inventario", "Gestiona el stock de materias primas, insumos y productos terminados en almacén.", "2200.00", 8, "08:00", "17:00"));

        // Puestos de Oficina/Diseño
        puestosData.add(createPuestoData("Diseñador Textil", "Crea diseños y patrones innovadores para nuevas colecciones de prendas y telas.", "3500.00", 8, "09:00", "18:00"));
        puestosData.add(createPuestoData("Patronista", "Desarrolla y adapta patrones de prendas a diferentes tallas y estilos.", "2800.00", 8, "08:30", "17:30"));
        puestosData.add(createPuestoData("Modelista", "Crea modelos físicos a partir de los diseños, probando ajuste y caída de las prendas.", "2700.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Asistente Administrativo de Producción", "Apoya en tareas administrativas, registro de datos y organización de documentos de producción.", "1900.00", 8, "09:00", "18:00"));
        puestosData.add(createPuestoData("Analista de Costos Textiles", "Analiza y optimiza los costos de producción, buscando eficiencia y rentabilidad.", "3000.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Responsable de Compras de Materia Prima", "Gestiona la adquisición de telas, hilos y otros materiales, negociando con proveedores.", "2900.00", 8, "09:00", "18:00"));

        // Puestos de Mantenimiento
        puestosData.add(createPuestoData("Mecánico de Máquinas de Coser", "Mantiene, diagnostica y repara máquinas de coser industriales.", "2600.00", 8, "08:00", "17:00"));
        puestosData.add(createPuestoData("Técnico de Mantenimiento Industrial", "Realiza el mantenimiento preventivo y correctivo de toda la maquinaria industrial.", "3000.00", 8, "09:00", "18:00"));

        for (java.util.Map<String, Object> data : puestosData) {
            String nombrePuesto = (String) data.get("nombrePuesto");

            Optional<Puesto> existingPuesto = puestoRepository.findByNombrePuesto(nombrePuesto);
            if (existingPuesto.isEmpty()) {
                Puesto puesto = new Puesto();
                puesto.setNombrePuesto(nombrePuesto);
                puesto.setDescripcion((String) data.get("descripcion"));
                puesto.setSalarioBase(new BigDecimal((String) data.get("salarioBase")));
                puesto.setJornadaLaboralHoras((Integer) data.get("jornadaLaboralHoras"));
                puesto.setHoraInicioJornada(LocalTime.parse((String) data.get("horaInicioJornada")));
                puesto.setHoraFinJornada(LocalTime.parse((String) data.get("horaFinJornada")));

                puestosExistentes.add(puestoRepository.save(puesto));
                System.out.println("Puesto '" + puesto.getNombrePuesto() + "' sembrado.");
            } else {
                puestosExistentes.add(existingPuesto.get());
                System.out.println("Puesto '" + nombrePuesto + "' ya existe, omitiendo.");
            }
        }
        System.out.println("Puestos textiles definidos sembrados/actualizados: " + puestosExistentes.size());
        return puestosExistentes;
    }
    private java.util.Map<String, Object> createPuestoData(String nombrePuesto, String descripcion, String salarioBase, int jornadaLaboralHoras, String horaInicioJornada, String horaFinJornada) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("nombrePuesto", nombrePuesto);
        data.put("descripcion", descripcion);
        data.put("salarioBase", salarioBase); // Lo mantengo como String para que BigDecimal lo parseé
        data.put("jornadaLaboralHoras", jornadaLaboralHoras);
        data.put("horaInicioJornada", horaInicioJornada);
        data.put("horaFinJornada", horaFinJornada);
        return data;
    }

    private void seedBancos() {
        System.out.println("Sembrando bancos...");
        String[] bancoNames = { "Banco de Credito del Peru", "BBVA Continental", "Interbank", "Scotiabank",
                "Banco de la Nacion", "Caja Huancayo", "Banco Falabella" };
        String[] bancoCodes = { "BCP", "BBVA", "INTB", "SCTB", "BN", "CHYO", "FALAB" };

        for (int i = 0; i < bancoNames.length; i++) {
            String name = bancoNames[i];
            String code = bancoCodes[i];
            // Usa Optional.isEmpty() para verificar si el banco ya existe
            if (bancoRepository.findByNombreBanco(name).isEmpty()) {
                Banco banco = new Banco();
                banco.setNombreBanco(name);
                banco.setCodigoBanco(code);
                bancoRepository.save(banco);
                System.out.println("  Banco '" + name + "' sembrado.");
            } else {
                System.out.println("  Banco '" + name + "' ya existe, omitiendo.");
            }
        }
        System.out.println("Bancos sembrados.");
    }
    
    private final String[] DISTRITOS_LIMA = {
        "Miraflores", "San Isidro", "Santiago de Surco", "La Molina", "San Borja", "Barranco",
        "Pueblo Libre", "Magdalena del Mar", "Lince", "Jesus Maria", "San Miguel", "Surquillo",
        "Chorrillos", "Los Olivos", "Independencia", "San Juan de Lurigancho", "Comas",
        "Villa El Salvador", "Villa Maria del Triunfo", "Ate", "Santa Anita", "El Agustino",
        "Breña", "La Victoria", "Cercado de Lima", "Callao", "Bellavista", "La Perla", "La Punta"
    };

    private final String[] PROVINCIAS_PERU = {
        "Lima", "Arequipa", "Cusco", "Trujillo", "Piura", "Chiclayo", "Iquitos", "Huancayo",
        "Tacna", "Ica", "Juliaca", "Pucallpa", "Chimbote", "Cajamarca", "Puno", "Huaraz",
        "Tarapoto", "Ayacucho"
    };
    private final String[] PROVINCIAS_PERU_CAPITALES = {
        "Arequipa", "Cusco", "Trujillo", "Piura", "Chiclayo", "Iquitos", "Huancayo",
        "Tacna", "Ica", "Juliaca", "Pucallpa", "Chimbote", "Cajamarca", "Puno", "Huaraz",
        "Tarapoto", "Ayacucho"
    };
    private final String[] DEPARTAMENTOS_PERU = {
        "Lima", "Arequipa", "Cusco", "La Libertad", "Piura", "Lambayeque", "Loreto", "Junín",
        "Tacna", "Ica", "Puno", "Áncash", "San Martín", "Ayacucho", "Cajamarca", "Huánuco",
        "Ucayali", "Madre de Dios", "Pasco", "Moquegua", "Tumbes", "Amazonas", "Huancavelica"
    };
    private final String[] NOMBRES_CALLES_GENERICOS = {
        "Avenida Principal", "Calle Libertad", "Jirón Unión", "Pasaje Los Álamos", "Avenida Sol",
        "Calle San Martín", "Jirón 28 de Julio", "Pasaje Las Flores", "Avenida Bolognesi",
        "Calle Grau", "Jirón Puno", "Pasaje La Paz", "Avenida Garcilaso", "Calle Manco Cápac"
    };
    private final RegimenLaboral[] regimenesLaboralesParaEmpleados = {RegimenLaboral.REGIMEN_GENERAL};
    public List<Empleado> seedEmpleados(int numEmpleadosToGenerate) {
        System.out.println("Sembrando " + numEmpleadosToGenerate + " empleados...");

        // Aseguramos que haya puestos y bancos antes de crear empleados
        seedPuestos(); 
        seedBancos(); // Genera los bancos si no existen

        List<Puesto> puestos = (List<Puesto>) puestoRepository.findAll();
        List<Banco> bancos = (List<Banco>) bancoRepository.findAll();

        if (puestos.isEmpty() || bancos.isEmpty()) {
            System.err.println(
                    "ERROR: No hay puestos o bancos en la base de datos para generar empleados. Ejecute 'seedPuestos' y 'seedBancos' primero.");
            return new ArrayList<>();
        }
        List<HistorialPuesto> historialPuestosToSave = new ArrayList<>();
        List<Empleado> generatedEmpleados = new ArrayList<>();

        
        Sexo[] sexos = Sexo.values();
        EstadoCivil[] estadosCiviles = EstadoCivil.values();
        

        for (int i = 0; i < numEmpleadosToGenerate; i++) {
            Empleado empleado = new Empleado();
            empleado.setNombres(faker.name().firstName());
            empleado.setApellidos(faker.name().lastName() + " " + faker.name().lastName());

            TipoDocumento tipoDocAsignado;
            double prob = random.nextDouble(); // Genera un número entre 0.0 y 1.0
            if (prob < 0.95) {
                tipoDocAsignado = TipoDocumento.DNI;
            } else  { 
                tipoDocAsignado = TipoDocumento.CE;
            } 
            empleado.setTipoDocumento(tipoDocAsignado);
            
            String numeroDocumentoGenerado;
            if (tipoDocAsignado == TipoDocumento.DNI) {
                numeroDocumentoGenerado = faker.number().digits(8); // DNI tiene 8 dígitos
            } else {
                numeroDocumentoGenerado = faker.regexify("[0-9]{9}"); // CE suele tener 9 dígitos numéricos
            }

            empleado.setNumeroDocumento(numeroDocumentoGenerado);

            // Fechas de nacimiento entre 20 y 50 años atrás (aprox)
            empleado.setFechaNacimiento(LocalDate.now().minusYears(faker.number().numberBetween(20, 50))
                    .minusDays(faker.number().numberBetween(0, 365)));

            empleado.setSexo(sexos[random.nextInt(sexos.length)]);
            empleado.setEstadoCivil(estadosCiviles[random.nextInt(estadosCiviles.length)]);

            empleado.setNacionalidad("Peruana");
            empleado.setCorreo(faker.internet().emailAddress());

            String departamentoAsignado = DEPARTAMENTOS_PERU[random.nextInt(DEPARTAMENTOS_PERU.length)];
            empleado.setDepartamento(departamentoAsignado);
            String provinciaAsignada;
            String distritoAsignado;
            if (departamentoAsignado.equals("Lima")) { 
                distritoAsignado = DISTRITOS_LIMA[random.nextInt(DISTRITOS_LIMA.length)];
                provinciaAsignada = "Lima"; // La provincia de estos distritos es Lima
            } else {
                // Para otros departamentos o el 20% restante de Lima
                provinciaAsignada = PROVINCIAS_PERU[random.nextInt(PROVINCIAS_PERU.length)];
                distritoAsignado = faker.address().city(); // Faker genera un nombre de ciudad genérico si no hay específico
                // Asegurar que la provincia no es "Lima" si el departamento no es "Lima" o falló el random
                if (empleado.getDepartamento().equals("Lima") && !provinciaAsignada.equals("Lima")) {
                    provinciaAsignada = "Lima"; // Forzar a Lima si el dpto es Lima
                } else {
                    provinciaAsignada = PROVINCIAS_PERU_CAPITALES[random.nextInt(PROVINCIAS_PERU_CAPITALES.length)];
                    distritoAsignado = provinciaAsignada;
                }
            }
            empleado.setProvincia(provinciaAsignada);
            empleado.setDistrito(distritoAsignado);
            String calle = NOMBRES_CALLES_GENERICOS[random.nextInt(NOMBRES_CALLES_GENERICOS.length)];
            String numeroCasa = faker.number().digits(random.nextInt(3) + 3); 
            empleado.setDireccionCompleta(String.format("%s %s, %s, %s, %s", calle, numeroCasa, distritoAsignado, provinciaAsignada, departamentoAsignado));
            
            // Fecha de ingreso: entre 1 y 5 años atrás (más realista para antigüedad)
            LocalDate fechaIngreso = LocalDate.now().minusYears(faker.number().numberBetween(1, 5))
                    .minusDays(faker.number().numberBetween(0, 365));
            empleado.setFechaIngreso(fechaIngreso);
            empleado.setEstado(true); // Activo por defecto
            empleado.setFechaCese(null); // No cesado al inicio

            // Asigna un puesto aleatorio de los sembrados
            Puesto puestoAsignado = puestos.get(random.nextInt(puestos.size()));
            empleado.setPuesto(puestoAsignado);

            // Asigna un régimen laboral relevante para el sector textil
            empleado.setRegimenLaboral(regimenesLaboralesParaEmpleados[random.nextInt(regimenesLaboralesParaEmpleados.length)]);

            // Aproximadamente 50% de los empleados tendrán hijos calificados
            empleado.setTieneHijosCalificados(random.nextDouble() < 0.5);

            SistemaPensiones[] sistemas = SistemaPensiones.values();
            empleado.setSistemaPensiones(sistemas[random.nextInt(sistemas.length)]);

            if (empleado.getSistemaPensiones() == SistemaPensiones.AFP) {
                empleado.setCodigoPension(faker.number().digits(random.nextInt(5) + 5)); // Código de 5 a 9 dígitos
                String[] afpNames = { "Prima AFP", "Habitat AFP", "Profuturo AFP", "Integra AFP" }; // AFPs comunes en
                                                                                                    // Perú
                empleado.setNombreAfp(afpNames[random.nextInt(afpNames.length)]);
            } else {
                empleado.setCodigoPension(null);
                empleado.setNombreAfp(null);
            }

            empleado.setNumeroCuentaBanco(faker.number().digits(random.nextInt(3) + 13));
            // Asigna un banco aleatorio de los sembrados
            empleado.setBanco(bancos.get(random.nextInt(bancos.size())));

            empleadoRepository.save(empleado);
            generatedEmpleados.add(empleadoRepository.save(empleado));
            HistorialPuesto historialPuesto = new HistorialPuesto();
            historialPuesto.setEmpleado(empleado); // Referencia al empleado recién guardado
            historialPuesto.setPuesto(puestoAsignado); // El puesto inicial
            historialPuesto.setFechaInicio(empleado.getFechaIngreso()); // La fecha de inicio del puesto es la fecha de ingreso
            historialPuesto.setFechaFin(null); // No tiene fecha fin si es el puesto actual del empleado

            historialPuestosToSave.add(historialPuesto);
            System.out.println("  Empleado '" + empleado.getNombres() + " " + empleado.getApellidos() + "' sembrado.");
        }
        historialPuestoRepository.saveAll(historialPuestosToSave);
        System.out.println("Empleados sembrados.");
        return generatedEmpleados;
    }

    public void seedParametrosLegales() {
        System.out.println("Sembrando parámetros legales...");

        // RMV (Remuneración Mínima Vital) actual en Perú es 1130
        
        String rmvCode = "RMV_PERU";
        if (parametroLegalRepository.findVigenteByCodigoAndFecha(rmvCode, LocalDate.now()).isEmpty()) { // Usar tu
                                                                                                        // método
            ParametroLegal rmv = new ParametroLegal();
            rmv.setCodigo(rmvCode);
            rmv.setDescripcion("Remuneración Mínima Vital");
            rmv.setValor(new BigDecimal("1130.00"));
            rmv.setFechaInicioVigencia(LocalDate.of(2022, 5, 1));
            rmv.setFechaFinVigencia(null); // Vigencia indefinida
            parametroLegalRepository.save(rmv);
            System.out.println("  Parámetro Legal '" + rmv.getDescripcion() + "' sembrado.");
        } else {
            System.out.println("  Parámetro Legal 'RMV_PERU' ya existe vigente, omitiendo.");
        }
        String uitCode = "UIT";
        // Si no existe un UIT vigente para hoy (o la fecha de inicio de 2025), lo creamos.
        if (parametroLegalRepository.findVigenteByCodigoAndFecha(uitCode, LocalDate.of(2025, 1, 1)).isEmpty()) {
            ParametroLegal uit = new ParametroLegal();
            uit.setCodigo(uitCode);
            uit.setDescripcion("Unidad Impositiva Tributaria");
            uit.setValor(new BigDecimal("5250.00")); // Valor estimado para 2025. Ajusta si tienes el valor oficial.
            uit.setFechaInicioVigencia(LocalDate.of(2025, 1, 1));
            uit.setFechaFinVigencia(null); // Vigencia indefinida (o hasta que cambie en un futuro año)
            parametroLegalRepository.save(uit);
            System.out.println("   Parámetro Legal '" + uit.getDescripcion() + "' sembrado.");
        } else {
            System.out.println("   Parámetro Legal 'UIT' ya existe vigente, omitiendo.");
        }
        System.out.println("Parámetros legales sembrados.");
    }

    public List<Planilla> seedHistoricalPlanillas(int numMonths) {
        System.out.println("Sembrando " + numMonths + " planillas históricas...");
        List<Planilla> generatedPlanillas = new ArrayList<>();

        // Aseguramos que existan empleados, puestos, conceptos y parámetros legales
        if (puestoRepository.count() == 0)
            seedPuestos();
        if (bancoRepository.count() == 0)
            seedBancos();
        if (empleadoRepository.count() == 0)
            seedEmpleados(20);
        if (conceptoPagoRepository.count() == 0)
            seedConceptosPago();
        if (parametroLegalRepository.count() == 0)
            seedParametrosLegales();

        List<Empleado> allEmpleados = empleadoRepository.findAll();
        if (allEmpleados.isEmpty()) {
            System.err.println(
                    "ERROR: No hay empleados en la base de datos para generar planillas. No se generarán planillas.");
            return new ArrayList<>();
        }

        // Buscar el concepto de Sueldo Base, Asignación Familiar, ONP, AFP, Essalud,
        // etc.
        Optional<ConceptoPago> sueldoBaseOpt = conceptoPagoRepository.findByNombreConcepto("Sueldo Base");
        Optional<ConceptoPago> asignacionFamiliarOpt = conceptoPagoRepository
                .findByNombreConcepto("Asignación Familiar");
        Optional<ConceptoPago> aporteOnpOpt = conceptoPagoRepository.findByNombreConcepto("Aporte ONP");
        Optional<ConceptoPago> aporteAfpFondoOpt = conceptoPagoRepository.findByNombreConcepto("Aporte AFP Fondo");
        Optional<ConceptoPago> comisionAfpOpt = conceptoPagoRepository.findByNombreConcepto("Comisión AFP");
        Optional<ConceptoPago> primaSeguroAfpOpt = conceptoPagoRepository.findByNombreConcepto("Prima Seguro AFP");
        Optional<ConceptoPago> essaludOpt = conceptoPagoRepository.findByNombreConcepto("EsSalud");
        Optional<ConceptoPago> horasExtrasOpt = conceptoPagoRepository.findByNombreConcepto("Horas Extras");
        Optional<ConceptoPago> descuentoTardanzaOpt = conceptoPagoRepository
                .findByNombreConcepto("Descuento por Tardanza");

        // Generar planillas para los últimos `numMonths`
        LocalDate today = LocalDate.now();
        for (int m = 0; m < numMonths; m++) {
            LocalDate planillaPeriodDate = today.minusMonths(m); // Fecha de periodo de la planilla (ej. 2024-06-15)
            int mes = planillaPeriodDate.getMonthValue();
            int anio = planillaPeriodDate.getYear();

            // Evitar duplicados: verificar si ya existe una planilla para este mes/año y
            // tipo MENSUAL
            if (planillaRepository.findByMesAndAnioAndTipoPlanilla(mes, anio, TipoPlanilla.MENSUAL).isPresent()) {
                System.out.println("  Planilla MENSUAL para " + mes + "/" + anio + " ya existe, omitiendo.");
                continue;
            }

            System.out.println("  Generando planilla MENSUAL para " + mes + "/" + anio + "...");

            Planilla planilla = new Planilla();
            planilla.setMes(mes);
            planilla.setAnio(anio);
            planilla.setTipoPlanilla(TipoPlanilla.MENSUAL);
            planilla.setFechaGenerada(LocalDate.now()); // Fecha de generación hoy

            // Buscar el RMV vigente para el *periodo de la planilla* (no necesariamente
            // hoy)
            BigDecimal rmvValueForPlanilla = RMV_DEFAULT;
            Optional<ParametroLegal> currentRmvParamOpt = parametroLegalRepository
                    .findVigenteByCodigoAndFecha("RMV_PERU", planillaPeriodDate);
            if (currentRmvParamOpt.isPresent()) {
                planilla.setParamRmv(currentRmvParamOpt.get());
                rmvValueForPlanilla = currentRmvParamOpt.get().getValor(); // Actualizar RMV si se encuentra uno
                                                                           // específico para este periodo
            } else {
                System.err.println("  ADVERTENCIA: No se encontró RMV para el periodo " + mes + "/" + anio
                        + ". Usando valor por defecto: " + RMV_DEFAULT);

            }

            // Una forma simple para el seeder sería:
            // planilla.setParamRmv(currentRmvParamOpt.orElse(null)); // Asigna null si no
            // se encuentra, si tu campo lo permite.
            List<DetallePlanilla> detalles = new ArrayList<>();

            for (Empleado empleado : allEmpleados) {
                // Solo genera si el empleado estaba activo en el mes de la planilla
                if (empleado.getFechaIngreso()
                        .isAfter(planillaPeriodDate.withDayOfMonth(planillaPeriodDate.lengthOfMonth())) ||
                        (empleado.getFechaCese() != null
                                && empleado.getFechaCese().isBefore(planillaPeriodDate.withDayOfMonth(1)))) {
                    continue; // Empleado no activo en este mes
                }

                LocalDate startOfMonth = planillaPeriodDate.withDayOfMonth(1);
                LocalDate endOfMonth = planillaPeriodDate.withDayOfMonth(planillaPeriodDate.lengthOfMonth());
                System.out.println("    Generando asistencias para " + empleado.getNombres() + " "
                        + empleado.getApellidos() + " en " + mes + "/" + anio);
                for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
                    // Simular que no todos los días son laborables o que hay algunas
                    // faltas/tardanzas
                    if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        continue; // No generar asistencias para fines de semana
                    }

                    if (asistenciaRepository.findByEmpleadoAndFecha(empleado, date).isPresent()) {
                        // System.out.println(" Asistencia para " + empleado.getNombres() + " el " +
                        // date + " ya existe, omitiendo.");
                        continue; // Si ya existe, saltar a la siguiente fecha
                    }
                    Asistencia asistencia = new Asistencia();
                    asistencia.setEmpleado(empleado);
                    asistencia.setFecha(date);

                    LocalTime horaEntrada;
                    LocalTime horaSalida;
                    int minutosTardanza = 0;
                    EstadoAsistencia estadoAsistencia = EstadoAsistencia.PRESENTE;
                    double prob = random.nextDouble();

                    if (prob < 0.05) { // 5% de probabilidad de falta completa
                        estadoAsistencia = EstadoAsistencia.AUSENTE;
                        horaEntrada = null; // Si está ausente, no hay hora de entrada
                        horaSalida = null; // Si está ausente, no hay hora de salida
                        minutosTardanza = 0; // No hay tardanza si está ausente
                    } else if (prob < 0.20) { // 15% de probabilidad de tardanza (0.05 a 0.20)
                        estadoAsistencia = EstadoAsistencia.TARDANZA;
                        int tardanzaMinutos = faker.number().numberBetween(16, 45);
                        horaEntrada = LocalTime.of(8, tardanzaMinutos);
                        horaSalida = LocalTime.of(17, faker.number().numberBetween(0, 30)); // Salida normal o un poco
                                                                                            // tarde
                        minutosTardanza = tardanzaMinutos - 15; // Suponiendo que la hora de entrada esperada es 8:00
                                                                
                    } else { // El resto (80% de probabilidad) es PRESENTE y PUNTUAL
                        estadoAsistencia = EstadoAsistencia.PRESENTE;
                        horaEntrada = LocalTime.of(8, faker.number().numberBetween(0, 15)); // Entrada entre 8:00 y 8:15
                                                                                            // (puntual)
                        horaSalida = LocalTime.of(17, faker.number().numberBetween(0, 30)); // Salida normal
                        minutosTardanza = 0;
                    }

                    asistencia.setHoraEntrada(horaEntrada);
                    asistencia.setHoraSalida(horaSalida);
                    asistencia.setEstado(estadoAsistencia);
                    asistencia.setMinutosTardanza(minutosTardanza);

                    asistenciaRepository.save(asistencia); // ¡Guardar la asistencia!
                }

                DetallePlanilla detalle = new DetallePlanilla();
                detalle.setPlanilla(planilla); // Asocia el detalle a la planilla
                detalle.setEmpleado(empleado);

                BigDecimal sueldoBase = empleado.getPuesto().getSalarioBase();
                // Calcular Asignación Familiar basada en el RMV vigente de la planilla
                BigDecimal asignacionFamiliar = BigDecimal.ZERO;
                if (empleado.getTieneHijosCalificados() != null && empleado.getTieneHijosCalificados()) {
                    asignacionFamiliar = rmvValueForPlanilla.multiply(new BigDecimal("0.10")).setScale(2,
                            RoundingMode.HALF_UP);
                }

                detalle.setSueldoBase(sueldoBase);
                detalle.setAsignacionFamiliar(asignacionFamiliar);

                BigDecimal remuneracionComputable = sueldoBase.add(asignacionFamiliar); // Simplificado
                detalle.setRemuneracionComputableAfecta(remuneracionComputable);

                BigDecimal totalIngresosAdicionales = BigDecimal.ZERO;
                BigDecimal totalDescuentos = BigDecimal.ZERO;
                BigDecimal totalAportesEmpleador = BigDecimal.ZERO;

                // --- Generar Movimientos de Planilla ---
                // Ingresos
                if (sueldoBaseOpt.isPresent()) {
                    MovimientoPlanilla movSueldo = new MovimientoPlanilla(null, detalle, sueldoBaseOpt.get(),
                            sueldoBase);
                    detalle.addMovimientoPlanilla(movSueldo);
                }
                if (asignacionFamiliar.compareTo(BigDecimal.ZERO) > 0 && asignacionFamiliarOpt.isPresent()) {
                    MovimientoPlanilla movAsignacion = new MovimientoPlanilla(null, detalle,
                            asignacionFamiliarOpt.get(), asignacionFamiliar);
                    detalle.addMovimientoPlanilla(movAsignacion);
                }

                // Horas Extras (aleatorias para algunos)
                if (random.nextDouble() < 0.3 && horasExtrasOpt.isPresent()) { // 30% de probabilidad
                    BigDecimal horasExtrasMonto = new BigDecimal(faker.number().numberBetween(50, 300)).setScale(2,
                            RoundingMode.HALF_UP);
                    MovimientoPlanilla movHorasExtras = new MovimientoPlanilla(null, detalle, horasExtrasOpt.get(),
                            horasExtrasMonto);
                    detalle.addMovimientoPlanilla(movHorasExtras);
                    totalIngresosAdicionales = totalIngresosAdicionales.add(horasExtrasMonto);
                }

                // Descuento por Tardanza (aleatorio para algunos)
                if (random.nextDouble() < 0.1 && descuentoTardanzaOpt.isPresent()) { // 10% de probabilidad
                    BigDecimal tardanzaMonto = new BigDecimal(faker.number().numberBetween(10, 50)).setScale(2,
                            RoundingMode.HALF_UP);
                    MovimientoPlanilla movTardanza = new MovimientoPlanilla(null, detalle, descuentoTardanzaOpt.get(),
                            tardanzaMonto.negate()); // Negativo para descuento
                    detalle.addMovimientoPlanilla(movTardanza);
                    totalDescuentos = totalDescuentos.add(tardanzaMonto);
                }

                // Descuentos por Sistema de Pensiones
                if (empleado.getSistemaPensiones() == SistemaPensiones.ONP && aporteOnpOpt.isPresent()) {
                    BigDecimal aporteOnpPorcentaje = aporteOnpOpt.get().getValorReferencial()
                            .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                    BigDecimal montoOnp = remuneracionComputable.multiply(aporteOnpPorcentaje).setScale(2,
                            RoundingMode.HALF_UP);
                    MovimientoPlanilla movOnp = new MovimientoPlanilla(null, detalle, aporteOnpOpt.get(),
                            montoOnp.negate());
                    detalle.addMovimientoPlanilla(movOnp);
                    totalDescuentos = totalDescuentos.add(montoOnp);
                } else if (empleado.getSistemaPensiones() == SistemaPensiones.AFP && aporteAfpFondoOpt.isPresent()
                        && comisionAfpOpt.isPresent() && primaSeguroAfpOpt.isPresent()) {
                    BigDecimal aporteFondoPorcentaje = aporteAfpFondoOpt.get().getValorReferencial()
                            .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                    BigDecimal comisionAfpPorcentaje = comisionAfpOpt.get().getValorReferencial()
                            .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                    BigDecimal primaSeguroPorcentaje = primaSeguroAfpOpt.get().getValorReferencial()
                            .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                    BigDecimal montoAfpFondo = remuneracionComputable.multiply(aporteFondoPorcentaje).setScale(2,
                            RoundingMode.HALF_UP);
                    BigDecimal montoComisionAfp = remuneracionComputable.multiply(comisionAfpPorcentaje).setScale(2,
                            RoundingMode.HALF_UP);
                    BigDecimal montoPrimaSeguroAfp = remuneracionComputable.multiply(primaSeguroPorcentaje).setScale(2,
                            RoundingMode.HALF_UP);

                    MovimientoPlanilla movAfpFondo = new MovimientoPlanilla(null, detalle, aporteAfpFondoOpt.get(),
                            montoAfpFondo.negate());
                    detalle.addMovimientoPlanilla(movAfpFondo);
                    totalDescuentos = totalDescuentos.add(montoAfpFondo);

                    MovimientoPlanilla movComisionAfp = new MovimientoPlanilla(null, detalle, comisionAfpOpt.get(),
                            montoComisionAfp.negate());
                    detalle.addMovimientoPlanilla(movComisionAfp);
                    totalDescuentos = totalDescuentos.add(montoComisionAfp);

                    MovimientoPlanilla movPrimaSeguroAfp = new MovimientoPlanilla(null, detalle,
                            primaSeguroAfpOpt.get(), montoPrimaSeguroAfp.negate());
                    detalle.addMovimientoPlanilla(movPrimaSeguroAfp);
                    totalDescuentos = totalDescuentos.add(montoPrimaSeguroAfp);
                }

                // Aportes del Empleador (EsSalud)
                if (essaludOpt.isPresent()) {
                    BigDecimal essaludPorcentaje = essaludOpt.get().getValorReferencial().divide(new BigDecimal("100"),
                            4, RoundingMode.HALF_UP);
                    BigDecimal montoEssalud = remuneracionComputable.multiply(essaludPorcentaje).setScale(2,
                            RoundingMode.HALF_UP);
                    MovimientoPlanilla movEssalud = new MovimientoPlanilla(null, detalle, essaludOpt.get(),
                            montoEssalud);
                    detalle.addMovimientoPlanilla(movEssalud);
                    totalAportesEmpleador = totalAportesEmpleador.add(montoEssalud);
                }

                // Calcular totales
                BigDecimal sueldoBruto = sueldoBase.add(asignacionFamiliar).add(totalIngresosAdicionales);
                BigDecimal sueldoNeto = sueldoBruto.subtract(totalDescuentos);

                detalle.setSueldoBruto(sueldoBruto);
                detalle.setSueldoNeto(sueldoNeto);
                detalle.setTotalIngresosAdicionales(totalIngresosAdicionales);
                detalle.setTotalDescuentos(totalDescuentos);
                detalle.setTotalAportesEmpleador(totalAportesEmpleador);

                detalles.add(detalle);
            }

            planilla.setDetallesPlanilla(detalles);
            generatedPlanillas.add(planillaRepository.save(planilla));
            System.out.println(
                    "  Planilla MENSUAL " + mes + "/" + anio + " sembrada con " + detalles.size() + " detalles.");
        }
        System.out.println("Planillas históricas sembradas.");
        return generatedPlanillas;
    }

    public void clearAllData() {
        System.out.println("Limpiando todos los datos...");
        try {
            movimientoPlanillaRepository.deleteAllInBatch();
            detallePlanillaRepository.deleteAllInBatch();
            asistenciaRepository.deleteAllInBatch();
            historialPuestoRepository.deleteAllInBatch();
            planillaRepository.deleteAllInBatch();
            empleadoRepository.deleteAllInBatch();
            puestoRepository.deleteAllInBatch();
            conceptoPagoRepository.deleteAllInBatch();
            bancoRepository.deleteAllInBatch();
            parametroLegalRepository.deleteAllInBatch();
            System.out.println("Datos limpiados.");
        } catch (Exception e) {
            System.err.println("Error durante la limpieza de datos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo al limpiar todos los datos: " + e.getMessage(), e);
        }
    }

    public void generateAllDemoData() {
        System.out.println("Iniciando generación de datos demo para Textilima...");
        //clearAllData();

        seedConceptosPago();
        seedBancos();
        seedPuestos();
        seedParametrosLegales();
        seedEmpleados(20);

        seedHistoricalPlanillas(6);

        System.out.println("Generación de datos demo finalizada para Textilima.");
    }
}
