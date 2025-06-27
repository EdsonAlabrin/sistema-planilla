package com.textilima.textilima.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Puesto;

@Service
public interface PuestoService {
   /**
     * Obtiene una lista de todos los puestos.
     * @return Una lista de objetos Puesto.
     */
    List<Puesto> getAllPuestos();

    /**
     * Obtiene un puesto por su ID.
     * @param id El ID del puesto.
     * @return Un Optional que contiene el Puesto si se encuentra, o vacío si no existe.
     */
    Optional<Puesto> getPuestoById(Integer idPuesto);

    /**
     * Guarda un nuevo puesto o actualiza uno existente.
     * @param puesto El objeto Puesto a guardar/actualizar.
     * @return El Puesto guardado/actualizado.
     */
    Puesto savePuesto(Puesto puesto);

    /**
     * Elimina un puesto por su ID.
     * @param id El ID del puesto a eliminar.
     */
    void deletePuesto(Integer idPuesto);

    /**
     * Busca un puesto por su nombre.
     * @param nombrePuesto El nombre del puesto.
     * @return Un Optional que contiene el Puesto si se encuentra, o vacío si no existe.
     */
    Optional<Puesto> getPuestoByNombrePuesto(String nombrePuesto);

    /**
     * Obtiene todos los puestos con un salario base mayor o igual al valor especificado.
     * @param salarioBase El salario base mínimo.
     * @return Una lista de puestos que cumplen con la condición.
     */
    List<Puesto> getPuestosBySalarioBaseGreaterThanEqual(BigDecimal salarioBase);
}
