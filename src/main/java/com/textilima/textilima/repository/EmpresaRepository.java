package com.textilima.textilima.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    /**
     * Busca una empresa por su número de RUC.
     * @param ruc El número de RUC de la empresa.
     * @return La empresa encontrada o null si no existe.
     */
    Optional<Empresa> findByRuc(String ruc);
}
