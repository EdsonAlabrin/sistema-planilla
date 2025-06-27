package com.textilima.textilima.controller;

import com.textilima.textilima.model.*;
import com.textilima.textilima.service.DetallePlanillaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/detallesPlanilla")

public class DetallePlanillaController {

    private final DetallePlanillaService detallePlanillaService;

    @Autowired
    public DetallePlanillaController(DetallePlanillaService detallePlanillaService) {
        this.detallePlanillaService = detallePlanillaService;
    }

    // Método para obtener un detalle de planilla por su ID
    @GetMapping("/{idDetalle}")
    public ResponseEntity<DetallePlanilla> obtenerDetalle(@PathVariable Integer idDetalle) {
        // Usamos findById que devuelve un Optional y lo manejamos con orElse(null) o .orElseThrow()
        Optional<DetallePlanilla> detalle = detallePlanillaService.findById(idDetalle);
        return detalle.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Método para obtener todos los detalles de planilla de una planilla específica
    @GetMapping("/planilla/{idPlanilla}")
    public ResponseEntity<List<DetallePlanilla>> obtenerPorPlanilla(@PathVariable Integer idPlanilla) {
        // Usamos findByPlanillaId que devuelve una lista
        List<DetallePlanilla> detalles = detallePlanillaService.findByPlanillaId(idPlanilla);
        return ResponseEntity.ok(detalles);
    }

    // Método para agregar un bono a un detalle de planilla existente
    @PostMapping("/{idDetalle}/bonos")
    public ResponseEntity<DetallePlanilla> agregarBono(
            @PathVariable Integer idDetalle,
            @RequestBody Bono bono) {
        try {
            DetallePlanilla updatedDetalle = detallePlanillaService.agregarBonoADetalle(idDetalle, bono);
            return ResponseEntity.ok(updatedDetalle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // O un ResponseEntity con mensaje de error
        }
    }

    // Método para agregar un descuento a un detalle de planilla existente
    @PostMapping("/{idDetalle}/descuentos")
    public ResponseEntity<DetallePlanilla> agregarDescuento(
            @PathVariable Integer idDetalle,
            @RequestBody Descuento descuento) {
        try {
            DetallePlanilla updatedDetalle = detallePlanillaService.agregarDescuentoADetalle(idDetalle, descuento);
            return ResponseEntity.ok(updatedDetalle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // O un ResponseEntity con mensaje de error
        }
    }

    // Método para generar una boleta para un detalle de planilla
    @PostMapping("/{idDetalle}/boleta")
    public ResponseEntity<DetallePlanilla> generarBoleta(@PathVariable Integer idDetalle) {
        // Asumiendo que generarBoleta retorna el DetallePlanilla actualizado
        try {
            DetallePlanilla updatedDetalle = detallePlanillaService.generarBoleta(idDetalle);
            return ResponseEntity.ok(updatedDetalle);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // O un ResponseEntity con mensaje de error
        }
    }

    // Otros métodos CRUD si los necesitas (ej. listar todos, crear, actualizar, eliminar)
    @GetMapping
    public ResponseEntity<List<DetallePlanilla>> listarTodosDetalles() {
        return ResponseEntity.ok(detallePlanillaService.findAll());
    }
}