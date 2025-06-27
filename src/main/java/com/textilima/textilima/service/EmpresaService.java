package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Empresa;

public interface EmpresaService {
    /**
     * Obtiene una lista de todas las empresas.
     * @return Una lista de objetos Empresa.
     */
    List<Empresa> getAllEmpresas();

    /**
     * Obtiene una empresa por su ID.
     * @param id El ID de la empresa.
     * @return Un Optional que contiene la Empresa si se encuentra, o vacío si no existe.
     */
    Optional<Empresa> getEmpresaById(Integer idEmpresa);

    /**
     * Guarda una nueva empresa o actualiza una existente.
     * @param empresa El objeto Empresa a guardar/actualizar.
     * @return La Empresa guardada/actualizada.
     */
    Empresa saveEmpresa(Empresa empresa);

    /**
     * Elimina una empresa por su ID.
     * @param id El ID de la empresa a eliminar.
     */
    void deleteEmpresa(Integer idEmpresa);

    /**
     * Busca una empresa por su número de RUC.
     * @param ruc El número de RUC de la empresa.
     * @return Un Optional que contiene la Empresa si se encuentra, o vacío si no existe.
     */
    Optional<Empresa> getEmpresaByRuc(String ruc);
}

