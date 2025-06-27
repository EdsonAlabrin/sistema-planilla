package com.textilima.textilima.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Rol;
import com.textilima.textilima.repository.RolRepository;
import com.textilima.textilima.service.RolService;

@Service
public class RolServiceImpl implements RolService{
    @Autowired // Inyección de dependencia del repositorio de Rol
    private RolRepository rolRepository;

    /**
     * Obtiene una lista de todos los roles.
     * @return Una lista de objetos Rol.
     */
    @Override
    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    /**
     * Obtiene un rol por su ID.
     * @param id El ID del rol.
     * @return Un Optional que contiene el Rol si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Rol> getRolById(Integer idRol) {
        return rolRepository.findById(idRol);
    }

    /**
     * Guarda un nuevo rol o actualiza uno existente.
     * @param rol El objeto Rol a guardar/actualizar.
     * @return El Rol guardado/actualizado.
     */
    @Override
    public Rol saveRol(Rol rol) {
        // Aquí se podría añadir lógica de negocio adicional antes de guardar,
        // por ejemplo, validaciones de nombre de rol.
        return rolRepository.save(rol);
    }

    /**
     * Elimina un rol por su ID.
     * @param id El ID del rol a eliminar.
     */
    @Override
    public void deleteRol(Integer idRol) {
        rolRepository.deleteById(idRol);
    }

    /**
     * Busca un rol por su nombre.
     * @param nombreRol El nombre del rol.
     * @return Un Optional que contiene el Rol si se encuentra, o vacío si no existe.
     */
    @Override
    public Optional<Rol> getRolByNombreRol(String nombreRol) {
        return Optional.ofNullable(rolRepository.findByNombreRol(nombreRol));
    }
}
