package com.textilima.textilima.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.textilima.textilima.model.Empresa;
import com.textilima.textilima.service.EmpresaService;

@Controller
@RequestMapping("/api/empresas")
public class EmpresaController {
    @Autowired // Inyección de dependencia del servicio de Empresa
    private EmpresaService empresaService;

    /**
     * Obtiene una lista de todas las empresas.
     * GET /api/empresas
     * @return Una lista de objetos Empresa.
     */
    @GetMapping
    public List<Empresa> getAllEmpresas() {
        return empresaService.getAllEmpresas();
    }

    /**
     * Obtiene una empresa por su ID.
     * GET /api/empresas/{id}
     * @param id El ID de la empresa a buscar.
     * @return ResponseEntity con la Empresa encontrada y estado OK, o NOT_FOUND si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> getEmpresaById(@PathVariable Integer id) {
        Optional<Empresa> empresa = empresaService.getEmpresaById(id);
        return empresa.map(ResponseEntity::ok) // Si la empresa está presente, retorna 200 OK con la empresa
                      .orElse(ResponseEntity.notFound().build()); // Si no, retorna 404 Not Found
    }

    /**
     * Crea una nueva empresa o actualiza una existente.
     * POST /api/empresas
     * @param empresa El objeto Empresa a crear/actualizar (en el cuerpo de la solicitud).
     * @return ResponseEntity con la Empresa guardada y estado CREATED si es nueva, o OK si es actualizada.
     */
    @PostMapping
    public ResponseEntity<Empresa> createOrUpdateEmpresa(@RequestBody Empresa empresa) {
        Empresa savedEmpresa = empresaService.saveEmpresa(empresa);
        // Para una única empresa, si ya existe, se actualizaría. Si es la primera vez, se crearía.
        // Podrías añadir lógica para diferenciar creación de actualización si el ID es nulo.
        return new ResponseEntity<>(savedEmpresa, HttpStatus.CREATED); // O HttpStatus.OK si es una actualización
    }

    /**
     * Elimina una empresa por su ID.
     * DELETE /api/empresas/{id}
     * @param id El ID de la empresa a eliminar.
     * @return ResponseEntity con estado NO_CONTENT si se elimina correctamente, o NOT_FOUND si no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Integer id) {
        if (empresaService.getEmpresaById(id).isPresent()) {
            empresaService.deleteEmpresa(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }

    /**
     * Busca una empresa por su número de RUC.
     * GET /api/empresas/by-ruc/{ruc}
     * @param ruc El número de RUC de la empresa a buscar.
     * @return ResponseEntity con la Empresa encontrada y estado OK, o NOT_FOUND si no existe.
     */
    @GetMapping("/by-ruc/{ruc}")
    public ResponseEntity<Empresa> getEmpresaByRuc(@PathVariable String ruc) {
        Optional<Empresa> empresa = empresaService.getEmpresaByRuc(ruc);
        return empresa.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
}
