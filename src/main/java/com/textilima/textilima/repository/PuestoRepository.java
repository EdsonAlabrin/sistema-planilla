package com.textilima.textilima.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Puesto;


@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Integer> {
    /**
     * Busca un puesto por su nombre.
     * @param nombrePuesto El nombre del puesto a buscar.
     * @return El puesto encontrado o null si no existe.
     */
    Puesto findByNombrePuesto(String nombrePuesto);

    /**
     * Busca todos los puestos con un salario base mayor o igual al valor especificado.
     * @param salarioBase El salario base mínimo.
     * @return Una lista de puestos que cumplen con la condición.
     */
    List<Puesto> findBySalarioBaseGreaterThanEqual(BigDecimal salarioBase);
}