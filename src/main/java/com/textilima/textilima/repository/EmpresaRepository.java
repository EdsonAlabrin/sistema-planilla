package com.textilima.textilima.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textilima.textilima.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    /**
     * Busca una empresa por su número de RUC.
     * @param ruc El número de RUC de la empresa.
     * @return La empresa encontrada o null si no existe.
     */
    Empresa findByRuc(String ruc);
}
