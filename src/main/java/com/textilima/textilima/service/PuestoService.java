package com.textilima.textilima.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Puesto;

public interface PuestoService {
    /**
     * Obtiene una lista de todos los puestos.
     * @return Una lista de objetos Puesto.
     */
    List<Puesto> listarTodosLosPuestos();

    /**
     * Obtiene un puesto por su ID.
     * @param id El ID del puesto.
     * @return Un Optional que contiene el Puesto si se encuentra, o vacío si no existe.
     */
    Optional<Puesto> obtenerPuestoPorId(Integer idPuesto);

    /**
     * Guarda un nuevo puesto.
     * @param puesto El objeto Puesto a guardar.
     * @return El Puesto guardado.
     */
    Puesto crearPuesto(Puesto puesto);

    /**
     * Actualiza un puesto existente.
     * @param id El ID del puesto a actualizar.
     * @param puesto El objeto Puesto con los datos actualizados.
     * @return El Puesto actualizado.
     */
    Puesto actualizarPuesto(Integer idPuesto, Puesto puesto);

    /**
     * Elimina un puesto por su ID.
     * @param id El ID del puesto a eliminar.
     */
    void eliminarPuesto(Integer idPuesto);

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
