// src/main/java/com/textilima/textilima/controller/PlanillaController.java
package com.textilima.textilima.controller;

// Importa todas las entidades del modelo que serán utilizadas en el controlador.
import com.textilima.textilima.model.*;
// Importa específicamente el enum TipoPlanilla de la entidad Planilla.
import com.textilima.textilima.model.Planilla.TipoPlanilla;
// Importa específicamente el enum TipoConcepto de la entidad ConceptoPago.
import com.textilima.textilima.model.ConceptoPago.TipoConcepto;
// Importa todos los repositorios para la interacción con la base de datos.
import com.textilima.textilima.repository.*;
// Importa el servicio de Boletas
import com.textilima.textilima.service.BoletaService;
// Importa el servicio de Empresa
import com.textilima.textilima.service.EmpresaService; // <-- Nuevo import

// Anotaciones de Spring para configurar el controlador.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Para pasar datos a las vistas Thymeleaf.
import org.springframework.web.bind.annotation.*; // Para mapear solicitudes HTTP (GET, POST, etc.).
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Para pasar mensajes después de una redirección.

// Para la validación de formularios.
import org.springframework.validation.BindingResult; // Para manejar errores de validación.
import org.springframework.validation.annotation.Validated;


// Utilidades de Java.
import java.math.BigDecimal; // Para manejar cálculos monetarios con precisión.
import java.math.RoundingMode; // Para definir el modo de redondeo en BigDecimal.
import java.time.LocalDate; // Para trabajar con fechas sin hora.
import java.time.Month; // Para obtener el nombre del mes.
import java.util.ArrayList; // Para listas dinámicas.
import java.util.Arrays; // Para trabajar con arrays.
import java.util.List; // Interfaz para colecciones de elementos.
import java.util.Optional; // Para manejar valores que pueden ser nulos.
import java.util.Comparator; // Para ordenar colecciones.

@Controller
@RequestMapping("/planillas")
public class PlanillaController {

    @Autowired
    private PlanillaRepository planillaRepository;

    @Autowired
    private DetallePlanillaRepository detallePlanillaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    @Autowired
    private ParametroLegalRepository parametroLegalRepository;

    @Autowired
    private ConceptoPagoRepository conceptoPagoRepository;

    @Autowired
    private MovimientoPlanillaRepository movimientoPlanillaRepository;

    @Autowired
    private BoletaService boletaService;

    @Autowired // <-- Ahora inyectamos el servicio
    private EmpresaService empresaService;

    @GetMapping
    public String redirectToPlanillasList() {
        return "redirect:/planillas/listar";
    }

    @GetMapping("/listar")
    public String listarPlanillas(Model model) {
        List<Planilla> planillas = planillaRepository.findAll();
        planillas.sort(Comparator
            .comparing(Planilla::getAnio).reversed()
            .thenComparing(Planilla::getMes).reversed()
        );
        model.addAttribute("planillas", planillas);
        return "planillas/listar";
    }

    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        Planilla newPlanilla = new Planilla();
        newPlanilla.setMes(LocalDate.now().getMonthValue());
        newPlanilla.setAnio(LocalDate.now().getYear());

        model.addAttribute("planilla", newPlanilla);
        model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
        return "planillas/form";
    }

    /**
     * Muestra el formulario para editar una planilla existente.
     * @param id El ID de la planilla a editar, mapeado desde la URL.
     * @param model El objeto Model para pasar datos a la vista.
     * @param redirectAttributes Para pasar mensajes de error si la planilla no se encuentra.
     * @return El nombre de la vista Thymeleaf (form.html) o una redirección.
     */
    @GetMapping("/editar/{id}") // URL: /planillas/editar/X
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) { // <-- CORRECCIÓN: @PathVariable("id") explícito
        Optional<Planilla> planillaOptional = planillaRepository.findById(id);
        if (planillaOptional.isPresent()) {
            model.addAttribute("planilla", planillaOptional.get());
            model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
            System.out.println("DEBUG (Editar Planilla): Planilla cargada: " + planillaOptional.get().getIdPlanilla() + ", Mes: " + planillaOptional.get().getMes() + ", Año: " + planillaOptional.get().getAnio());
            System.out.println("DEBUG (Editar Planilla): Tipos de Planilla cargados: " + Arrays.asList(TipoPlanilla.values()));
            return "planillas/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Planilla no encontrada.");
            System.err.println("ERROR: Planilla con ID " + id + " no encontrada para edición.");
            return "redirect:/planillas/listar";
        }
    }


    @PostMapping("/guardar")
    public String savePlanilla(@ModelAttribute @Validated Planilla planilla,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
            model.addAttribute("error", "Por favor, corrija los errores en el formulario.");
            return "planillas/form";
        }

        if (planilla.getIdPlanilla() == null) {
            planilla.setFechaGenerada(LocalDate.now());
            
            LocalDate fechaBusquedaRmv = LocalDate.of(planilla.getAnio(), planilla.getMes(), 1);
            ParametroLegal rmv = parametroLegalRepository.findVigenteByCodigoAndFecha("RMV", fechaBusquedaRmv)
                                                      .orElse(parametroLegalRepository.findTopByCodigoOrderByFechaInicioVigenciaDesc("RMV")
                                                      .orElse(null));
            
            if (rmv == null) {
                redirectAttributes.addFlashAttribute("error", "No se encontró un Parámetro Legal (RMV) vigente para el " + planilla.getMes() + "/" + planilla.getAnio() + ". Por favor, registre uno.");
                model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values()));
                return "planillas/form";
            }
            planilla.setParamRmv(rmv);
        } else {
             Planilla existingPlanilla = planillaRepository.findById(planilla.getIdPlanilla())
                                                        .orElseThrow(() -> new RuntimeException("Planilla no encontrada para edición"));
             planilla.setFechaGenerada(existingPlanilla.getFechaGenerada());
             planilla.setParamRmv(existingPlanilla.getParamRmv());
        }

        planillaRepository.save(planilla);
        redirectAttributes.addFlashAttribute("success", "Planilla guardada exitosamente!");
        return "redirect:/planillas/listar";
    }

    /**
     * Genera una planilla con cálculos, recibiendo el objeto Planilla del formulario.
     * Este método se usa cuando se CREA una nueva planilla y se quiere generar directamente con detalles.
     * @param planilla El objeto Planilla con los datos del formulario (mes, anio, tipoPlanilla).
     * @param result Resultado de la validación.
     * @param redirectAttributes Para mensajes de redirección.
     * @param model El objeto Model para pasar datos a la vista si hay errores.
     * @return Redirección a la lista de planillas o al formulario si hay errores.
     */
    @PostMapping("/generar-con-calculos")
    public String generarPlanillaConCalculos(@ModelAttribute @Validated Planilla planilla, // <-- ¡CAMBIO AQUÍ!
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes,
                                  Model model) { // Asegura que Model también esté presente para pasar tiposPlanilla
        
        if (result.hasErrors()) {
            // Si hay errores de validación, vuelve al formulario
            model.addAttribute("tiposPlanilla", Arrays.asList(TipoPlanilla.values())); // Vuelve a pasar los tipos
            model.addAttribute("error", "Por favor, corrija los errores en el formulario para generar la planilla.");
            System.out.println("DEBUG: Errores de validación al generar con cálculos: " + result.getAllErrors());
            return "planillas/form";
        }

        // Lógica de validación de duplicados y RMV
        if (planillaRepository.findByMesAndAnioAndTipoPlanilla(planilla.getMes(), planilla.getAnio(), planilla.getTipoPlanilla()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Ya existe una planilla '" + planilla.getTipoPlanilla().getDisplayName() + "' para " + planilla.getMes() + "/" + planilla.getAnio() + ". No se puede generar una duplicada.");
            return "redirect:/planillas/listar";
        }

        LocalDate fechaBusquedaRmv = LocalDate.of(planilla.getAnio(), planilla.getMes(), 1);
        ParametroLegal rmv = parametroLegalRepository.findVigenteByCodigoAndFecha("RMV", fechaBusquedaRmv)
                                                      .orElse(parametroLegalRepository.findTopByCodigoOrderByFechaInicioVigenciaDesc("RMV")
                                                      .orElse(null));

        if (rmv == null) {
             redirectAttributes.addFlashAttribute("error", "No se encontró un Parámetro Legal (RMV) para " + planilla.getMes() + "/" + planilla.getAnio() + ". Por favor, registre uno.");
             return "redirect:/planillas/listar";
        }

        // Asigna los campos que no vienen del formulario directamente (fechaGenerada, paramRmv)
        planilla.setFechaGenerada(LocalDate.now());
        planilla.setParamRmv(rmv);
        planillaRepository.save(planilla); // Guarda la planilla base

        // ... El resto de tu lógica para generar los detalles de planilla y movimientos
        List<Empleado> empleados = empleadoRepository.findAll();
        empleados.forEach(empleado -> {
            if (empleado.getPuesto() == null) {
                System.err.println("ADVERTENCIA: Empleado " + empleado.getNombres() + " " + empleado.getApellidos() + " no tiene un puesto asignado. Se omitirá del cálculo de planilla.");
            }
        });

        Optional<ConceptoPago> asignacionFamiliarOptional = conceptoPagoRepository.findByNombreConceptoAndTipo("Asignación Familiar", TipoConcepto.INGRESO);
        Optional<ConceptoPago> essaludOptional = conceptoPagoRepository.findByNombreConceptoAndTipo("Essalud", TipoConcepto.APORTE_EMPLEADOR);
        Optional<ConceptoPago> onpOptional = conceptoPagoRepository.findByNombreConceptoAndTipo("Aporte ONP", TipoConcepto.DESCUENTO);
        Optional<ConceptoPago> afpOptional = conceptoPagoRepository.findByNombreConceptoAndTipo("Aporte AFP Total", TipoConcepto.DESCUENTO);

        ConceptoPago asignacionFamiliarConcepto = asignacionFamiliarOptional.orElse(null);
        ConceptoPago essaludConcepto = essaludOptional.orElse(null);
        ConceptoPago onpConcept = onpOptional.orElse(null);
        ConceptoPago afpConcept = afpOptional.orElse(null);

        List<DetallePlanilla> detallePlanillaList = new ArrayList<>();
        BigDecimal rmvValor = rmv.getValor();

        for (Empleado empleado : empleados) {
            if (empleado.getPuesto() == null) {
                continue;
            }

            DetallePlanilla detalle = new DetallePlanilla();
            detalle.setPlanilla(planilla);
            detalle.setEmpleado(empleado);

            BigDecimal sueldoBase = empleado.getPuesto().getSalarioBase();
            detalle.setSueldoBase(sueldoBase);

            BigDecimal asignacionFamiliar = BigDecimal.ZERO;
            if (empleado.getTieneHijosCalificados() != null && empleado.getTieneHijosCalificados() && asignacionFamiliarConcepto != null) {
                asignacionFamiliar = rmvValor.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            }
            detalle.setAsignacionFamiliar(asignacionFamiliar);

            BigDecimal totalIngresosAdicionales = BigDecimal.ZERO;
            detalle.setTotalIngresosAdicionales(totalIngresosAdicionales);

            BigDecimal remuneracionComputableAfecta = sueldoBase.add(asignacionFamiliar).add(totalIngresosAdicionales);
            detalle.setRemuneracionComputableAfecta(remuneracionComputableAfecta);

            BigDecimal sueldoBruto = sueldoBase.add(asignacionFamiliar).add(totalIngresosAdicionales);
            detalle.setSueldoBruto(sueldoBruto);

            BigDecimal totalDescuentos = BigDecimal.ZERO;
            List<MovimientoPlanilla> movimientos = new ArrayList<>();

            if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.ONP && onpConcept != null) {
                BigDecimal onpMonto = remuneracionComputableAfecta.multiply(onpConcept.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                totalDescuentos = totalDescuentos.add(onpMonto);
                movimientos.add(new MovimientoPlanilla(null, detalle, onpConcept, onpMonto));
            } else if (empleado.getSistemaPensiones() == Empleado.SistemaPensiones.AFP && afpConcept != null) {
                BigDecimal afpMonto = remuneracionComputableAfecta.multiply(afpConcept.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                totalDescuentos = totalDescuentos.add(afpMonto);
                movimientos.add(new MovimientoPlanilla(null, detalle, afpConcept, afpMonto));
            }

            detalle.setTotalDescuentos(totalDescuentos);

            BigDecimal sueldoNeto = sueldoBruto.subtract(totalDescuentos);
            detalle.setSueldoNeto(sueldoNeto);

            BigDecimal totalAportesEmpleador = BigDecimal.ZERO;
            if (essaludConcepto != null) {
                BigDecimal essaludMonto = remuneracionComputableAfecta.multiply(essaludConcepto.getValorReferencial()).setScale(2, RoundingMode.HALF_UP);
                totalAportesEmpleador = totalAportesEmpleador.add(essaludMonto);
                movimientos.add(new MovimientoPlanilla(null, detalle, essaludConcepto, essaludMonto));
            }

            detalle.setTotalAportesEmpleador(totalAportesEmpleador);

            detalle.setMovimientosPlanilla(movimientos);
            detallePlanillaList.add(detalle);
        }

        detallePlanillaRepository.saveAll(detallePlanillaList);

        redirectAttributes.addFlashAttribute("success", "Planilla de " + planilla.getTipoPlanilla().getDisplayName() + " para " + Month.of(planilla.getMes()).name() + "/" + planilla.getAnio() + " generada exitosamente!");
        System.out.println("DEBUG: Planilla generada con cálculos: " + planilla);
        return "redirect:/planillas/listar";
    }

    /**
     * Muestra los detalles de una planilla específica, incluyendo los detalles de cada empleado.
     * @param idPlanilla El ID de la planilla cuyos detalles se desean ver, mapeado desde la URL.
     * @param model El objeto Model para pasar datos a la vista.
     * @param redirectAttributes Para pasar mensajes de error si la planilla no se encuentra.
     * @return El nombre de la vista Thymeleaf (planillaDetalles.html) o una redirección.
     */
    @GetMapping("/{id}/detalles") // URL: /planillas/X/detalles
    public String showPlanillaDetails(@PathVariable("id") Integer idPlanilla, Model model, RedirectAttributes redirectAttributes) { // <-- CORRECCIÓN: @PathVariable("id") explícito
        Optional<Planilla> planillaOptional = planillaRepository.findById(idPlanilla);
        if (planillaOptional.isPresent()) {
            Planilla planilla = planillaOptional.get();
            List<DetallePlanilla> detallesPlanilla = detallePlanillaRepository.findByPlanilla(planilla);
            
            detallesPlanilla.forEach(dp -> {
                dp.setMovimientosPlanilla(movimientoPlanillaRepository.findByDetallePlanilla(dp));
                Optional<Boleta> boletaExistente = boletaService.getBoletaByDetallePlanilla(dp);
                if (boletaExistente.isPresent()) {
                    dp.setBoletaGenerada(true);
                    dp.setBoleta(boletaExistente.get());
                } else {
                    dp.setBoletaGenerada(false);
                }
            });

            model.addAttribute("planilla", planilla);
            model.addAttribute("detallesPlanilla", detallesPlanilla);
            return "planillas/planillaDetalles";
        } else {
            redirectAttributes.addFlashAttribute("error", "Planilla no encontrada.");
            return "redirect:/planillas/listar";
        }
    }


    /**
     * Elimina una planilla y sus detalles asociados.
     * @param id El ID de la planilla a eliminar, recibido como RequestParam.
     * @param redirectAttributes Para mensajes de redirección.
     * @return Redirección a la lista de planillas.
     */
    @PostMapping("/eliminar")
    public String deletePlanilla(@RequestParam("id") Integer idPlanilla, RedirectAttributes redirectAttributes) {
        try {
            Optional<Planilla> planillaOptional = planillaRepository.findById(idPlanilla);
            if (planillaOptional.isPresent()) {
                Planilla planillaToDelete = planillaOptional.get();

                // 1. Eliminar movimientos de planilla asociados a los detalles de esta planilla
                List<DetallePlanilla> detalles = detallePlanillaRepository.findByPlanilla(planillaToDelete);
                for (DetallePlanilla detalle : detalles) {
                    // Asegúrate de que los movimientos de planilla sean eliminados
                    // antes de los detalles de planilla, si hay una FK.
                    // Si tienes @OneToMany(cascade = CascadeType.ALL) en Planilla y DetallePlanilla,
                    // y en DetallePlanilla hacia MovimientoPlanilla,
                    // esta parte podría ser manejada automáticamente por JPA.
                    // Pero para mayor seguridad, o si no tienes cascade configurado, hazlo manualmente.
                    movimientoPlanillaRepository.deleteAll(detalle.getMovimientosPlanilla());
                }

                // 2. Eliminar los detalles de planilla asociados
                detallePlanillaRepository.deleteAll(detalles);

                // 3. Finalmente, eliminar la planilla
                planillaRepository.delete(planillaToDelete);

                redirectAttributes.addFlashAttribute("success", "Planilla eliminada exitosamente!");
                System.out.println("DEBUG: Planilla con ID " + idPlanilla + " eliminada.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Planilla no encontrada para eliminar.");
                System.err.println("ERROR: Planilla con ID " + idPlanilla + " no encontrada para eliminación.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la planilla: " + e.getMessage());
            System.err.println("ERROR: Excepción al eliminar planilla con ID " + idPlanilla + ": " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/planillas/listar";
    }
    /**
     * Muestra el detalle completo de un empleado dentro de una planilla específica.
     * Esto incluye todos los movimientos de ingresos, descuentos y aportes para ese empleado.
     * @param id El ID del DetallePlanilla (no el ID del empleado).
     * @param model El objeto Model para pasar datos a la vista.
     * @param redirectAttributes Para pasar mensajes de error si el detalle no se encuentra.
     * @return El nombre de la vista Thymeleaf (empleadoDetalles.html) o una redirección.
     */
    @GetMapping("/detalle-empleado/{id}")
    public String showDetalleEmpleado(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<DetallePlanilla> detalleOptional = detallePlanillaRepository.findById(id);
        if (detalleOptional.isPresent()) {
            DetallePlanilla detalle = detalleOptional.get();
            detalle.setMovimientosPlanilla(movimientoPlanillaRepository.findByDetallePlanillaId(detalle.getIdDetalle()));
            model.addAttribute("detallePlanilla", detalle);
            return "planillas/empleadoDetalles";
        } else {
            redirectAttributes.addFlashAttribute("error", "Detalle de planilla no encontrado.");
            return "redirect:/planillas/listar";
        }
    }

    /**
     * Endpoint para generar una boleta para un detalle de planilla específico.
     * Esta acción creará un registro de Boleta en la BD.
     * @param idDetalle El ID del DetallePlanilla para el que se generará la boleta.
     * @param redirectAttributes Para pasar mensajes de éxito o error.
     * @return Redirección a la página de detalles de la planilla.
     */
    @PostMapping("/generar-boleta/{idDetalle}")
    public String generarBoleta(@PathVariable Integer idDetalle, RedirectAttributes redirectAttributes) {
        Optional<DetallePlanilla> detalleOptional = detallePlanillaRepository.findById(idDetalle);
        if (detalleOptional.isPresent()) {
            DetallePlanilla detalle = detalleOptional.get();

            // Verificar si ya existe una boleta para este detalle de planilla
            Optional<Boleta> existingBoleta = boletaService.getBoletaByDetallePlanilla(detalle);
            if (existingBoleta.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Ya existe una boleta generada para este empleado en esta planilla.");
                return "redirect:/planillas/" + detalle.getPlanilla().getIdPlanilla() + "/detalles";
            }

            // Aquí se "generaría" la boleta. Para esta implementación, solo creamos el registro en la BD.
            // En un caso real, aquí se llamaría a un servicio de PDF para generar el archivo.
            String rutaPdfSimulada = "/boletas/" + detalle.getPlanilla().getAnio() + "_" + detalle.getPlanilla().getMes() + "_" + detalle.getEmpleado().getNumeroDocumento() + ".pdf";
            Boleta nuevaBoleta = new Boleta(detalle, LocalDate.now(), rutaPdfSimulada);
            boletaService.saveBoleta(nuevaBoleta);

            redirectAttributes.addFlashAttribute("success", "Boleta generada exitosamente para " + detalle.getEmpleado().getNombres() + " " + detalle.getEmpleado().getApellidos() + ".");
            return "redirect:/planillas/" + detalle.getPlanilla().getIdPlanilla() + "/detalles";
        } else {
            redirectAttributes.addFlashAttribute("error", "Detalle de planilla no encontrado para generar boleta.");
            return "redirect:/planillas/listar";
        }
    }

    /**
     * Endpoint para ver la boleta (simulando la visualización del PDF o HTML).
     * @param idBoleta El ID de la Boleta a visualizar.
     * @param model El objeto Model para pasar los datos de la boleta a la vista.
     * @param redirectAttributes Para pasar mensajes de error.
     * @return El nombre de la vista HTML que representará la boleta.
     */
    @GetMapping("/ver-boleta/{idBoleta}")
    public String verBoleta(@PathVariable Integer idBoleta, Model model, RedirectAttributes redirectAttributes) {
        Optional<Boleta> boletaOptional = boletaService.getBoletaById(idBoleta);
        if (boletaOptional.isPresent()) {
            Boleta boleta = boletaOptional.get();
            // Carga explícitamente los movimientos de planilla para que estén disponibles en la vista
            DetallePlanilla detalle = boleta.getDetallePlanilla();
            detalle.setMovimientosPlanilla(movimientoPlanillaRepository.findByDetallePlanillaId(detalle.getIdDetalle()));
            
            model.addAttribute("boleta", boleta);
            model.addAttribute("detallePlanilla", detalle); // Pasamos el detallePlanilla para la información del empleado y cálculos

            // --- Obtener y pasar los datos de la empresa ---
            // Ahora usamos el servicio para obtener la empresa
            Optional<Empresa> empresaOptional = empresaService.getOneCompany();
            if (empresaOptional.isPresent()) {
                model.addAttribute("empresa", empresaOptional.get());
            } else {
                model.addAttribute("empresa", null); // O pasar un objeto Empresa vacío/default
            }
            // --- FIN de Obtener y pasar los datos de la empresa ---

            return "boletas/verBoleta";
        } else {
            redirectAttributes.addFlashAttribute("error", "Boleta no encontrada.");
            return "redirect:/planillas/listar";
        }
    }

      /**
     * Endpoint para eliminar una boleta.
     * @param idBoleta El ID de la Boleta a eliminar.
     * @param redirectAttributes Para pasar mensajes.
     * @return Redirección a la página de detalles de la planilla.
     */
    @PostMapping("/eliminar-boleta/{idBoleta}")
    public String eliminarBoleta(@PathVariable("idBoleta") Integer idBoleta, RedirectAttributes redirectAttributes) {
        Optional<Boleta> boletaOptional = boletaService.getBoletaById(idBoleta);
        if (boletaOptional.isPresent()) {
            Integer idPlanilla = boletaOptional.get().getDetallePlanilla().getPlanilla().getIdPlanilla();
            boletaService.deleteBoleta(idBoleta);
            redirectAttributes.addFlashAttribute("success", "Boleta eliminada exitosamente.");
            return "redirect:/planillas/" + idPlanilla + "/detalles";
        } else {
            redirectAttributes.addFlashAttribute("error", "Boleta no encontrada para eliminación.");
            return "redirect:/planillas/listar";
        }
    }
}
