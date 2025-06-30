package com.textilima.textilima.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Empresa;
import com.textilima.textilima.repository.EmpresaRepository;
import com.textilima.textilima.service.EmpresaService;

@Service
public class EmpresaServiceImpl implements EmpresaService {
    @Autowired // Inyección de dependencia del repositorio de Empresa
    private EmpresaRepository empresaRepository;

    /**
     * Obtiene una lista de todas las empresas.
     * En este caso de uso con una única empresa, retornará una lista con un máximo de un elemento.
     * @return Una lista de objetos Empresa.
     */
    @Override
    public List<Empresa> getAllEmpresas() {
        return empresaRepository.findAll();
    }

    /**
     * Obtiene una empresa por su ID.
     * @param id El ID de la empresa.
     * @return Un Optional que contiene la Empresa si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Empresa> getEmpresaById(Integer idEmpresa) {
        return empresaRepository.findById(idEmpresa);
    }

    /**
     * Guarda una nueva empresa o actualiza una existente.
     * Para el caso de una única empresa, se puede usar para crear la primera y luego actualizarla.
     * @param empresa El objeto Empresa a guardar/actualizar.
     * @return La Empresa guardada/actualizada.
     */
    @Override
    public Empresa saveEmpresa(Empresa empresa) {
        // Aquí podrías añadir lógica de negocio adicional antes de guardar,
        // por ejemplo, validaciones específicas para la única empresa.
        return empresaRepository.save(empresa);
    }

    /**
     * Elimina una empresa por su ID.
     * Para el caso de una única empresa, esta operación no sería común, pero la funcionalidad se mantiene.
     * @param id El ID de la empresa a eliminar.
     */
    @Override
    public void deleteEmpresa(Integer idEmpresa) {
        empresaRepository.deleteById(idEmpresa);
    }

     /**
     * Busca una empresa por su número de RUC.
     * @param ruc El número de RUC de la empresa.
     * @return Un Optional que contiene la Empresa si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Empresa> getEmpresaByRuc(String ruc) {
        return empresaRepository.findByRuc(ruc);
    }

    /**
     * Obtiene la primera empresa registrada.
     * Útil cuando se espera que solo haya una configuración de empresa.
     * @return Un Optional que contiene la primera Empresa encontrada, o vacío si no hay ninguna.
     */
    @Override
    public Optional<Empresa> getOneCompany() {
        List<Empresa> empresas = empresaRepository.findAll();
        return empresas.isEmpty() ? Optional.empty() : Optional.of(empresas.get(0));
    }
}
